package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import de.f0rce.ace.AceEditor;
import de.f0rce.ace.enums.AceMode;
import de.f0rce.ace.enums.AceTheme;
import de.f0rce.ace.util.AceCursorPosition;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.jpa.entity.ElementType;
import ru.numbdev.interviewer.page.component.abstracts.AbstractBuilderListComponent;

import java.util.List;
import java.util.stream.Collectors;

public class CreateElementDialogComponent extends Dialog {

    private static final String QUESTION_ID = "question";
    private static final String VALUE_ID = "value";
    private static final String ANSWERS_ID = "answers";
    private static final String CODE_ID = "code";
    private int questionCount = 0;

    private Select<String> select;
    private VerticalLayout questionForm;
    private VerticalLayout textForm;
    private VerticalLayout codeForm;

    private ElementValues elementValue;

    public CreateElementDialogComponent(
            ElementAction<ElementValues> saveAction,
            ElementAction<ElementValues> removeAction
    ) {
        this(saveAction, removeAction, null);
    }

    public CreateElementDialogComponent(
            ElementAction<ElementValues> saveAction,
            ElementAction<ElementValues> removeAction,
            ElementValues values
    ) {
        this.elementValue = values;
        initDialog(saveAction, removeAction);
        open();
    }

    private void initDialog(
            ElementAction<ElementValues> saveAction,
            ElementAction<ElementValues> removeAction
    ) {
        getElement().setAttribute("aria-label", "Add note");

        getHeader().add(createHeaderLayout());
        createFooter(saveAction, removeAction);

        VerticalLayout dialogLayout = createDialogLayout();
        add(dialogLayout);
        setModal(false);
        setDraggable(true);
    }

    private H2 createHeaderLayout() {
        H2 headline = new H2("Добавить блок");
        headline.addClassName("draggable");
        headline.getStyle().set("margin", "0").set("font-size", "1.5em")
                .set("font-weight", "bold").set("cursor", "move")
                .set("padding", "var(--lumo-space-m) 0").set("flex", "1");

        return headline;
    }

    private VerticalLayout createDialogLayout() {
        select = new Select<>();
        select.setLabel("Тип блока");
        select.setItems(ElementType.getNames());
        if (elementValue != null) {
            select.setValue(elementValue.type().getName());
        }
        select.addValueChangeListener(e -> {
            switch (ElementType.getByName(e.getValue())) {
                case TEXT -> {
                    textForm.setVisible(true);
                    questionForm.setVisible(false);
                    codeForm.setVisible(false);
                }
                case QUESTION -> {
                    textForm.setVisible(false);
                    questionForm.setVisible(true);
                    codeForm.setVisible(false);
                }
                case CODE -> {
                    textForm.setVisible(false);
                    questionForm.setVisible(false);
                    codeForm.setVisible(true);
                }
            }
        });

        buildDialogQuestion();
        buildDialogText();
        buildDialogCode();
        setSizeUndefined();
        var fieldLayout = new VerticalLayout(select, questionForm, textForm, codeForm);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        fieldLayout.getStyle().set("width", "300px").set("max-width", "100%");

        return fieldLayout;
    }

    private void buildDialogQuestion() {
        boolean isNotNullQuestion = elementValue != null && elementValue.type() == ElementType.QUESTION;
        questionForm = new VerticalLayout();
        var descriptionArea = new TextArea("Текст вопроса");
        descriptionArea.setSizeFull();
        descriptionArea.setId(QUESTION_ID);
        if (isNotNullQuestion) {
            descriptionArea.setValue(elementValue.description());
        }

        var answers = new VerticalLayout();
        answers.setId(ANSWERS_ID);
        answers.setSizeFull();

        if (isNotNullQuestion) {
            parseValueForQuestion()
                    .stream()
                    .map(e -> {
                        var field = new TextField();
                        field.setId(questionCount + "_" + VALUE_ID);
                        field.setPlaceholder("Ответ");
                        field.setValue(e);
                        field.setSizeFull();

                        questionCount += 1;
                        return field;
                    })
                    .forEach(answers::add);
        }

        var addButton = new Button();
        addButton.setId("add");
        addButton.setText("Добавить ответ");
        addButton.addClickListener(e -> buildQuestionField());

        questionForm.add(descriptionArea, answers, addButton);
        questionForm.setVisible(isNotNullQuestion);
        questionForm.setSizeFull();
    }

    private List<String> parseValueForQuestion() {
        return AbstractBuilderListComponent.parseValuesQuestion(elementValue.value());
    }

    private void buildQuestionField() {
        var field = new TextField();
        field.setId(questionCount + "_" + VALUE_ID);
        field.setPlaceholder("Ответ");
        field.setSizeFull();

        questionForm
                .getChildren()
                .filter(e -> e.getId().get().contains(ANSWERS_ID))
                .map(e -> (VerticalLayout) e)
                .forEach(e -> e.add(field));

        questionCount += 1;
    }

    private void buildDialogText() {
        boolean isNotNullText = elementValue != null && elementValue.type() == ElementType.TEXT;
        textForm = new VerticalLayout();
        var questionText = new TextArea("Текст вопроса");
        questionText.setSizeFull();
        questionText.setId(QUESTION_ID);
        if (isNotNullText) {
            questionText.setValue(elementValue.description());
        }

        var valueText = new TextArea("Описание");
        valueText.setSizeFull();
        valueText.setId(VALUE_ID);
        if (isNotNullText) {
            valueText.setValue(elementValue.value());
        }

        textForm.add(questionText, valueText);
        textForm.setVisible(isNotNullText);
        textForm.setSizeFull();
    }

    private void buildDialogCode() {
        boolean isNotNullCode = elementValue != null && elementValue.type() == ElementType.CODE;
        codeForm = new VerticalLayout();

        var code = new AceEditor();
        code.setId(CODE_ID);
        code.setTheme(AceTheme.github);
        code.setMode(AceMode.java);
        code.addAceChangedListener(a -> justTest(a.getSource(), a.getValue()));

        if (isNotNullCode) {
            code.setValue(elementValue.value());
        }

        codeForm.add(code);
        codeForm.setVisible(isNotNullCode);
        codeForm.setSizeFull();
    }

    private static int count = 0;
    private AceCursorPosition carriage;
    private void justTest(AceEditor editor, String value) {
//        editor.addTextAtPosition(count, 0, "1111111111111111111111111111111111111111111");
        count += 1;
    }

    private void createFooter(
            ElementAction<ElementValues> saveAction,
            ElementAction<ElementValues> removeAction
            ) {
        var saveButton = new Button("Сохранить", e -> this.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> doSaveAction(saveAction));

        var cancelButton = new Button("Отмена", e -> this.close());

        var removeButton = new Button("Удалить");
        removeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        removeButton.addClickListener(e -> doRemoveAction(removeAction));
        if (elementValue == null) {
            removeButton.setVisible(false);
        }


        getFooter().add(saveButton);
        getFooter().add(cancelButton);
        getFooter().add(removeButton);
    }

    private void doRemoveAction(ElementAction<ElementValues> removeAction) {
        var type = ElementType.getByName(select.getValue());

        var values = switch (type) {
            case QUESTION -> valuesFromQuestion();
            case TEXT -> valuesFromText();
            case CODE -> valuesFromCode();
        };

        removeAction.action(values);
        this.close();
    }

    private void doSaveAction(ElementAction<ElementValues> saveAction) {
        var type = ElementType.getByName(select.getValue());

        var values = switch (type) {
            case QUESTION -> valuesFromQuestion();
            case TEXT -> valuesFromText();
            case CODE -> valuesFromCode();
        };

        saveAction.action(values);
        this.close();
    }

    private ElementValues valuesFromText() {
        var description = textForm
                .getChildren()
                .filter(e -> e.getId().get().contains(QUESTION_ID))
                .map(e -> (TextArea) e)
                .findFirst()
                .get()
                .getValue();

        var value = textForm
                .getChildren()
                .filter(e -> e.getId().get().contains(VALUE_ID))
                .map(e -> (TextArea) e)
                .findFirst()
                .get()
                .getValue();

        return new ElementValues(
                elementValue != null ? elementValue.id() : null,
                ElementType.TEXT,
                description,
                value
        );
    }

    private ElementValues valuesFromQuestion() {
        var description = questionForm
                .getChildren()
                .filter(e -> e.getId().get().contains(QUESTION_ID))
                .map(e -> (TextArea) e)
                .findFirst()
                .get()
                .getValue();

        var value = questionForm
                .getChildren()
                .filter(e -> e.getId().get().contains(ANSWERS_ID))
                .flatMap(Component::getChildren)
                .filter(e -> e.getId().get().contains(VALUE_ID))
                .map(e -> ((TextField) e).getValue())
                .collect(Collectors.joining(","));

        return new ElementValues(
                elementValue != null ? elementValue.id() : null,
                ElementType.QUESTION,
                description,
                value
        );
    }

    private ElementValues valuesFromCode() {
        var value = codeForm
                .getChildren()
                .filter(e -> e.getId().get().contains(CODE_ID))
                .map(e -> (AceEditor) e)
                .findFirst()
                .get()
                .getValue();

        return new ElementValues(
                elementValue != null ? elementValue.id() : null,
                ElementType.CODE,
                null,
                value
        );
    }
}
