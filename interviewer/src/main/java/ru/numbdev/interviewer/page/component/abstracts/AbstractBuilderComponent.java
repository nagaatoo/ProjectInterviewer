package ru.numbdev.interviewer.page.component.abstracts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.jpa.entity.ElementType;
import ru.numbdev.interviewer.page.component.CustomEditor;
import ru.numbdev.interviewer.page.component.CustomRadioButtonsGroup;
import ru.numbdev.interviewer.page.component.CustomTextArea;

public abstract class AbstractBuilderComponent extends VerticalLayout {

    protected ElementValues buildValueFromComponent(Component component) {
        return switch (component) {
            case CustomTextArea ta -> valueFromTextArea(ta);
            case CustomRadioButtonsGroup rb -> valueFromRadioButton(rb);
            case CustomEditor te -> valueFromCodeEditor(te);
            default -> throw new UnsupportedOperationException();
        };
    }

    private ElementValues valueFromTextArea(CustomTextArea ta) {
        return new ElementValues(
                ta.getId().get(),
                ElementType.TEXT,
                ta.getLabel(),
                ta.getValue()
        );
    }

    private ElementValues valueFromRadioButton(CustomRadioButtonsGroup rb) {
        return new ElementValues(
                rb.getId().get(),
                ElementType.QUESTION,
                rb.getLabel(),
                rb.parseValueFromRadioButton()
        );
    }

    private ElementValues valueFromCodeEditor(CustomEditor te) {
        return new ElementValues(
                te.getId().get(),
                ElementType.CODE,
                null,
                te.getValue()
        );
    }

    protected Component buildElement(ElementValues value) {
        return buildElement(value.id(), value.type(), value.description(), value.value());
    }

    protected Component buildElement(String id, ElementType type, String description, String value) {
        return switch (type) {
            case QUESTION -> buildRadioButton(id, description, value);
            case TEXT -> buildTextField(id, description, value);
            case CODE -> buildCode(id, value);
        };
    }

    protected Component buildTextField(String id, String description, String value) {
        return new CustomTextArea(id, description, value);
    }

    protected Component buildRadioButton(String id, String description, String value) {
        return new CustomRadioButtonsGroup(id, description, value);
    }

    private Component buildCode(String id, String value) {
        return new CustomEditor(id, value);
    }
}
