package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.textfield.TextArea;
import ru.numbdev.interviewer.page.component.abstracts.EditableComponent;

import java.util.Map;

public class CustomTextArea extends TextArea implements EditableComponent {

    private long lastEventTime;

    private String actualState;

    public CustomTextArea(String id, String description, String value) {
        setId(id);
        setLabel(description);
        offerDiff(Map.of(1, value), Long.MAX_VALUE);

        lastEventTime = System.currentTimeMillis();
    }

    @Override
    public void setDiff(String actualState) {
        this.actualState = actualState;
    }

    @Override
    public Map<Integer, String> getDiff() {
        return Map.of(1, actualState);
    }

    @Override
    public void offerDiff(Map<Integer, String> diff, long eventTime) {
        if (lastEventTime > eventTime) {
            return;
        }

        lastEventTime = eventTime;
        setValue(diff.get(1));
    }

    @Override
    public void setReadOnlyMode() {
        setReadOnly(true);
    }
}
