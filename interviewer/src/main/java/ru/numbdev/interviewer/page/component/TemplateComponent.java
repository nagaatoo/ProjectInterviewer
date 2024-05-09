package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.textfield.TextField;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.jpa.entity.TemplateEntity;
import ru.numbdev.interviewer.enums.BuilderType;
import ru.numbdev.interviewer.page.component.abstracts.AbstractBuilderListComponent;
import ru.numbdev.interviewer.service.crud.TemplateCrudService;

import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class TemplateComponent extends AbstractBuilderListComponent {

    private final TemplateCrudService templateCrudService;

    private boolean isEditable;
    private boolean isList;
    private TemplateEntity template;

    private final TextField templateName = new TextField();
    private final Grid<com.vaadin.flow.component.Component> tasks = new Grid<>(
            com.vaadin.flow.component.Component.class, false);

    public void init(boolean isList, UUID templateId, ElementAction<ElementValues> action) {
        this.isList = isList;

        commonInit(templateId);
        if (isList) {
            tasks.addItemDoubleClickListener(chooseElementForInterview(action));
        }
    }

    public void init(boolean isEditable, UUID templateId) {
        this.isEditable = isEditable;

        commonInit(templateId);
        if (isEditable) {
            add(initEditPanel(e -> addAction()));
            tasks.addItemDoubleClickListener(chooseElementForUpdate());
        }
    }

    private void commonInit(UUID templateId) {
        if (templateId != null) {
            super.init(templateId);
            template = templateCrudService.findById(templateId);
        } else {
            template = templateCrudService.save(new TemplateEntity());
        }

        templateName.setPlaceholder("Название шаблона");
        templateName.setValue(
                StringUtils.isNotBlank(template.getName())
                        ? template.getName()
                        : "Без названия"
        );

        templateName.addBlurListener(e -> {
            if (StringUtils.isNotBlank(templateName.getValue())) {
                templateCrudService.save(template.setName(templateName.getValue()));
            }
        });
        if (isList || !isEditable) {
            templateName.setReadOnly(true);
        }

        add(templateName);
        add(tasks);
        buildDataProvider();

        setSizeFull();
    }

    public ElementValues getSelectedElement() {
        var selected = tasks.getSelectedItems();

        return CollectionUtils.isEmpty(selected)
                ? null
                : getElementValue(tasks.getSelectedItems().stream().findFirst().get().getId().get());
    }

    private void addAction() {
        add(new CreateElementDialogComponent(getSaveAction(), getRemoveAction()));
    }

    private ComponentEventListener<ItemDoubleClickEvent<com.vaadin.flow.component.Component>> chooseElementForUpdate() {
        return e -> {
            add(new CreateElementDialogComponent(
                            getSaveAction(),
                            getRemoveAction(),
                            getElementValue(e.getItem().getId().get())
                    )
            );
        };
    }

    private ComponentEventListener<ItemDoubleClickEvent<com.vaadin.flow.component.Component>> chooseElementForInterview(
            ElementAction<ElementValues> action
    ) {
        return e -> {
            action.action(getElementValue(e.getItem().getId().get()));
        };
    }

    private ElementAction<ElementValues> getSaveAction() {
        return a -> {
            if (a.id() != null) {
                saveExistsElement(a);
            } else {
                createElement(a.type(), a.description(), a.value(), template);
            }

            refresh();
        };
    }

    private ElementAction<ElementValues> getRemoveAction() {
        return a -> {
            deleteElement(a.id());
            refresh();
        };
    }

    private void buildDataProvider() {
        tasks.addComponentColumn(e -> e);
        refresh();
    }

    private void refresh() {
        tasks.setItems(
                getElements()
                        .stream()
                        .peek(this::muteElement)
                        .toList()
        );
    }

    private void muteElement(com.vaadin.flow.component.Component component) {
        if (component instanceof CustomRadioButtonsGroup rb) {
            rb.setReadOnly(true);
        }

        if (component instanceof CustomTextArea ta) {
            ta.setReadOnly(true);
//            ta.setEnabled(false);
        }

        if (component instanceof CustomEditor ee) {
            ee.setReadOnly(true);
        }
    }

    @Override
    protected BuilderType getType() {
        return BuilderType.TEMPLATE;
    }
}
