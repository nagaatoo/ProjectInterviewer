package ru.numbdev.interviewer.page.component.abstracts;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.jpa.entity.BuilderItemEntity;
import ru.numbdev.interviewer.jpa.entity.ElementType;
import ru.numbdev.interviewer.jpa.entity.HistoryBuilderItemEntity;
import ru.numbdev.interviewer.jpa.entity.QuestionnaireEntity;
import ru.numbdev.interviewer.jpa.entity.TemplateEntity;
import ru.numbdev.interviewer.enums.BuilderType;
import ru.numbdev.interviewer.service.crud.BuilderItemCrudService;
import ru.numbdev.interviewer.service.crud.HistoryItemCrudService;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

public abstract class AbstractBuilderListComponent extends AbstractBuilderComponent {

    @Autowired // Чтобы не тащить через конструктор
    protected BuilderItemCrudService builderItemCrudService;

    @Autowired
    protected HistoryItemCrudService historyItemCrudService;

    private List<ElementValues> items = new ArrayList<>();

    private boolean isHistory = false;

    protected void init(UUID entityId) {
        items = switch (getType()) {
            case QUESTIONNAIRE -> loadQuestionnaire(entityId);
            case TEMPLATE -> loadTemplate(entityId);
        };
    }

    protected void initAsHistory(UUID interviewId, UUID entityId) {
        this.isHistory = true;
        items = switch (getType()) {
            case QUESTIONNAIRE -> loadQuestionnaireAsHistory(interviewId, entityId);
            case TEMPLATE -> throw new UnsupportedOperationException("TEMPLATE is not supported");
        };
    }

    private List<ElementValues> loadQuestionnaire(UUID questionnaireId) {
        return this.builderItemCrudService
                .getItemsForQuestionnaire(questionnaireId)
                .stream()
                .map(this::buildValueFromItem)
                .toList();
    }

    private List<ElementValues> loadTemplate(UUID templateId) {
        return this.builderItemCrudService
                .getItemsForTemplate(templateId)
                .stream()
                .map(this::buildValueFromItem)
                .toList();
    }

    private List<ElementValues> loadQuestionnaireAsHistory(UUID interviewId, UUID questionnaireId) {
        var history = this.historyItemCrudService.findByInterviewerIdAndQuestionnaireId(interviewId, questionnaireId);

        if (CollectionUtils.isEmpty(history)) {
            return loadQuestionnaire(questionnaireId);
        }

        return  history
                        .stream()
                        .map(this::buildValueFromHistory)
                        .toList();
    }

    protected HorizontalLayout initEditPanel(ComponentEventListener<ClickEvent<Button>> addListener) {
        var hl = new HorizontalLayout();

        var addButton = new Button();
        addButton.setText("Добавить " + getType().getName());
        addButton.addClickListener(addListener);
        hl.add(addButton);

        return hl;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    protected abstract BuilderType getType();

    protected void saveExistsElement(ElementValues values) {
        var element = items
                .stream()
                .filter(i -> i.id().equals(values.id()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Element with id {0} not found",
                        values.id())));

        if (isHistory) {
            var entity = historyItemCrudService.getById(UUID.fromString(element.id()));
            historyItemCrudService.save(
                    entity
                            .setElementType(values.type())
                            .setElementValue(values.value())
                            .setElementDescription(values.description())
            );
        } else {
            var entity = builderItemCrudService.getById(UUID.fromString(element.id()));
            builderItemCrudService.save(
                    entity
                            .setElementType(values.type())
                            .setElementValue(values.value())
                            .setElementDescription(values.description())
            );
        }
    }

//    protected void saveExistsElement(Component component) {
//        var id = String.valueOf(component.getId());
//        var entity = items
//                .stream()
//                .filter(i -> i.getId().equals(UUID.fromString(id)))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Element with id {0} not found", id)));
//
//        switch (entity.getElementType()) {
//            case QUESTION -> saveValueFromRadioButton(entity, component);
//            case TEXT -> saveValueFromTextArea(entity, component);
//        }
//    }

//    private void saveValueFromRadioButton(BuilderItemEntity entity, Component component) {
//        var radioButtonGroup = (CustomRadioButtonsGroup) component;
//        entity.setElementDescription(radioButtonGroup.getLabel());
//        entity.setElementValue(radioButtonGroup.parseValueFromRadioButton());
//    }
//
//    private void saveValueFromTextArea(BuilderItemEntity entity, Component component) {
//        var area = (CustomTextArea) component;
//        builderItemCrudService.save(entity.setElementDescription(area.getLabel()).setElementValue(area.getValue()));
//    }

    protected List<Component> getElements() {
        return items
                .stream()
                .sorted(Comparator.comparing(ElementValues::created))
                .map(item ->
                        buildElement(
                                item.id(),
                                item.type(),
                                item.description(),
                                item.value()
                        )
                )
                .toList();
    }

    @SuppressWarnings("all")
    protected ElementValues getElementValue(String id) {
        return items
                .stream()
                .filter(e -> e.id().toString().equals(id))
                .findFirst()
                .get();
    }

    protected void createElement(ElementType type, String description, String value, QuestionnaireEntity entity) {
        var element = builderItemCrudService.save(createEntity(type, description, value).setQuestionnaire(entity));
        items.add(buildValueFromItem(element));
    }

    protected void createElement(ElementType type, String description, String value, TemplateEntity entity) {
        var element = builderItemCrudService.save(createEntity(type, description, value).setTemplate(entity));
        items.add(buildValueFromItem(element));
    }

    private ElementValues buildValueFromItem(BuilderItemEntity entity) {
        return new ElementValues(
                entity.getId().toString(),
                entity.getElementType(),
                entity.getElementDescription(),
                entity.getElementValue(),
                entity.getCreated()
        );
    }

    private ElementValues buildValueFromHistory(HistoryBuilderItemEntity entity) {
        return new ElementValues(
                entity.getId().toString(),
                entity.getElementType(),
                entity.getElementDescription(),
                entity.getElementValue(),
                buildCreated(entity.getElementOrder())
        );
    }

    // TODO придумать что делать
    private LocalDateTime buildCreated(Integer order) {
        var baseDate = LocalDateTime.MIN;

        return baseDate.plusYears(order);
    }

    protected void deleteElement(String id) {
        var element = items
                .stream()
                .filter(i -> i.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Element with id {0} not found", id)));

        builderItemCrudService.deleteById(UUID.fromString(element.id()));
        items.remove(element);
    }

    private BuilderItemEntity createEntity(ElementType type, String description, String value) {
        return new BuilderItemEntity()
                .setElementDescription(description)
                .setElementType(type)
                .setElementValue(value);
    }

    public static List<String> parseValuesQuestion(String items) {
        return List.of(items.split(","));
    }
}
