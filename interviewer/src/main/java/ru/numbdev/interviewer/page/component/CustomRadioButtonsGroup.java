package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import org.apache.commons.lang3.StringUtils;
import ru.numbDev.common.utils.ElementUtils;
import ru.numbdev.interviewer.page.component.abstracts.EditableComponent;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomRadioButtonsGroup extends RadioButtonGroup<String> implements EditableComponent {

    private String items;
    private String actualState;
    private long lastEventTime;

    private final Lock lock = new ReentrantLock();

    public CustomRadioButtonsGroup(String id, String description, String value) {
        this.items = value;

        setId(id);
        addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        var parsedValue = ElementUtils.parseRadioButtonFromValue(value);
        setLabel(description);
        setItems(parsedValue.getValues());
        setValue(parsedValue.getSelected());

        lastEventTime = System.currentTimeMillis();
    }

    @Override
    public void setDiff(String actualState) {
        try {
            lock.lock();
            this.actualState = actualState;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<Integer, String> getDiff() {
        try {
            lock.lock();
            if (StringUtils.isEmpty(actualState)) {
                return Map.of();
            }

            var result = Map.of(1, actualState);
            actualState = null;
            return result;

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void offerDiff(Map<Integer, String> diff, long eventTime) {
        if (lastEventTime > eventTime) {
            return;
        }

        var diffVal = diff.get(1);
        if (StringUtils.isBlank(diffVal)) {
            return;
        }

        lastEventTime = eventTime;
        setValue(diffVal);
        items = parseValueFromRadioButton();
    }

    @Override
    public void setReadOnlyMode() {
        setReadOnly(true);
    }

    public String parseValueFromRadioButton() {
        var selected = getValue();
        return ElementUtils.parseValueFromRadioButton(selected, items);
    }

}
