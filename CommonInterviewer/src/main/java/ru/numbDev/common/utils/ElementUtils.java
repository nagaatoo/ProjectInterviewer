package ru.numbDev.common.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import ru.numbDev.common.dto.KeyValueRadioButton;

public class ElementUtils {
    private ElementUtils() {
    }

    public static Map<Integer, String> buildRowsElement(String value) {
        var seq = new AtomicInteger();
        return Arrays
                .stream(value.split("\n"))
                .collect(
                        Collectors.toConcurrentMap(
                                e -> seq.incrementAndGet(),
                                e -> e
                        )
                );
    }

    // Формат: Foo, Boo, #Coo#, Doo
    public static KeyValueRadioButton parseRadioButtonFromValue(String value) {
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
                        .map(ElementUtils::cleanTags)
                        .toList()
        );

        return builder.build();
    }

    public static String parseValueFromRadioButton(String selected, String items) {
        if (StringUtils.isBlank(selected)) {
            return cleanTags(items);
        }

        return items.replace(selected, "#" + selected + "#");
    }

    public static String parseValueFromRadioButton(String selected, Map<Integer, String> itemsMap) {
        String items = cleanTags(itemsMap.get(1));
        return parseValueFromRadioButton(selected, items);
    }

    private static String cleanTags(String str) {
        return str.replace("#", "");
    }
}
