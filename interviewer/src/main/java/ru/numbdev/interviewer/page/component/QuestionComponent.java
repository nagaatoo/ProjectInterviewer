package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.textfield.TextField;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.enums.BuilderType;
import ru.numbdev.interviewer.enums.QuestionComponentType;
import ru.numbdev.interviewer.jpa.entity.QuestionnaireEntity;
import ru.numbdev.interviewer.page.component.abstracts.AbstractBuilderListComponent;
import ru.numbdev.interviewer.service.crud.QuestionsCrudService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class QuestionComponent extends AbstractBuilderListComponent {

    private QuestionComponentType type;

    private final QuestionsCrudService questionsCrudService;

    private QuestionnaireEntity questionnaire;

    private final TextField questionnaireName = new TextField();
    private final Grid<com.vaadin.flow.component.Component> tasks = new Grid<>(
            com.vaadin.flow.component.Component.class, false);

    public void init(QuestionComponentType type, UUID questionnaireId) {
        this.type = type;

        if (questionnaireId != null) {
            super.init(questionnaireId);
            questionnaire = questionsCrudService.findById(questionnaireId);
        } else {
            questionnaire = questionsCrudService.save(new QuestionnaireEntity());
        }

        switch (type) {
            case EDITABLE -> buildEditable();
            case INTERVIEW -> buildInterview();
            case REVIEW -> buildReview();
        }
    }

    public void initReview(UUID interviewId, UUID questionnaireId) {
        this.type = QuestionComponentType.REVIEW;
        super.initAsHistory(interviewId, questionnaireId);
        questionnaire = questionsCrudService.findById(questionnaireId);

        buildReview();
    }

    private void buildEditable() {
        questionnaireName.setPlaceholder("Название опросника");
        questionnaireName.setValue(
                StringUtils.isNotBlank(questionnaire.getName())
                        ? questionnaire.getName()
                        : "Без названия"
        );
        questionnaireName.addBlurListener(e -> {
            if (StringUtils.isNotBlank(questionnaireName.getValue())) {
                questionsCrudService.save(questionnaire.setName(questionnaireName.getValue()));
            }
        });

        add(questionnaireName);
        add(tasks);
        add(initEditPanel(e -> addAction()));

        buildDataProvider();
        tasks.addItemDoubleClickListener(chooseElement());

        setSizeFull();
    }

    private void buildInterview() {
        add(tasks);
        buildDataProvider();

        setSizeFull();
    }

    private void buildReview() {
        tasks.setSelectionMode(Grid.SelectionMode.NONE);
        add(tasks);
        buildDataProvider();

        setSizeFull();
    }

    private void addAction() {
        add(new CreateElementDialogComponent(getSaveAction(), getRemoveAction()));
    }

    private ComponentEventListener<ItemDoubleClickEvent<com.vaadin.flow.component.Component>> chooseElement() {
        return e -> {
            add(new CreateElementDialogComponent(
                            getSaveAction(),
                            getRemoveAction(),
                            getElementValue(e.getItem().getId().get())
                    )
            );
        };
    }

    private ElementAction<ElementValues> getSaveAction() {
        return a -> {
            if (a.id() != null) {
                saveExistsElement(a);
            } else {
                createElement(a.type(), a.description(), a.value(), questionnaire);
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
                type == QuestionComponentType.INTERVIEW
                        ? getElements().stream().peek(this::setSizeFullToElement).peek(this::muteElement).toList()
                        : getElements().stream().peek(this::setSizeFullToElement).toList()
        );
    }

    private void muteElement(com.vaadin.flow.component.Component component) {
        if (component instanceof CustomRadioButtonsGroup rb) {
            rb.setReadOnly(true);
        }

        if (component instanceof CustomTextArea ta) {
            ta.setReadOnly(true);
            ta.setEnabled(false);
        }

        if (component instanceof CustomEditor ee) {
            ee.setReadOnly(true);
        }
    }

    private void setSizeFullToElement(com.vaadin.flow.component.Component element) {
        var typed = (HasSize) element;
        typed.setSizeFull();
    }

    public List<ElementValues> getAnswers() {
        List<ElementValues> answers = new ArrayList<>();
        for(int i = 0; i<tasks.getDataCommunicator().getDataProviderSize(); i++) {
            var element = tasks.getDataCommunicator().getItem(i);
            answers.add(buildValueFromComponent(element));
        }

        return answers;
    }

    @Override
    protected BuilderType getType() {
        return BuilderType.QUESTIONNAIRE;
    }
}
