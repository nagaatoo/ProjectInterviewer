package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import org.apache.commons.lang3.StringUtils;
import ru.numbdev.interviewer.dto.KeyValueRadioButton;
import ru.numbdev.interviewer.page.component.abstracts.EditableComponent;

import java.util.Arrays;
import java.util.Map;

public class CustomRadioButtonsGroup extends RadioButtonGroup<String> implements EditableComponent {

    private String items;

    public CustomRadioButtonsGroup(String id, String description, String value) {
        this.items = value;

        setId(id);
        addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        var parsedValue = parseRadioButtonFromValue(value);
        setLabel(description);
        setItems(parsedValue.getValues());
        setValue(parsedValue.getSelected());
    }

    @Override
    public Map<Integer, String> getDiff(String actualState) {
        return Map.of(1, actualState);
    }

    @Override
    public void offerDiff(Map<Integer, String> diff) {
        setValue(diff.get(1));
        items = parseValueFromRadioButton();
    }

    @Override
    public void setReadOnlyMode() {
        setReadOnly(true);
    }

    // Формат: Foo, Boo, #Coo#, Doo
    private KeyValueRadioButton parseRadioButtonFromValue(String value) {
        var builder = KeyValueRadioButton.builder();
        if (StringUtils.isBlank(value)) {
            return builder.build();
        }

        var parts = value.split(",");
        builder.values(
                Arrays
                        .stream(parts)
                        .peek(v -> {
                            if (v.contains("#")) {
                                builder.selected(cleanTags(v));
                            }
                        })
                        .map(this::cleanTags)
                        .toList()
        );

        return builder.build();
    }

    public String parseValueFromRadioButton() {
        var selected = getValue();
        if (StringUtils.isBlank(selected)) {
            return cleanTags(items);
        }

        return items.replace(selected, "#" + selected + "#");
    }

    private String cleanTags(String str) {
        return str.replace("#", "");
    }
}
