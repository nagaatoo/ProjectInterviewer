package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.textfield.TextArea;
import ru.numbdev.interviewer.page.component.abstracts.EditableComponent;

import java.util.Map;

public class CustomTextArea extends TextArea implements EditableComponent {

    public CustomTextArea(String id, String description, String value) {
        setId(id);
        setLabel(description);
        offerDiff(Map.of(1, value));
    }

    @Override
    public Map<Integer, String> getDiff(String actualState) {
        return Map.of(1, actualState);
    }

    @Override
    public void offerDiff(Map<Integer, String> diff) {
        setValue(diff.get(1));
    }

    @Override
    public void setReadOnlyMode() {
        setReadOnly(true);
    }
}
