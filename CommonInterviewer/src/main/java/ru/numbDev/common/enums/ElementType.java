package ru.numbDev.common.enums;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

public enum ElementType {
    QUESTION("Тест"),
    TEXT("Поле"),
    CODE("Код");

    @Getter
    private final String name;

    ElementType(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        return Arrays.stream(values()).map(ElementType::getName).toList();
    }

    public static ElementType getByName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .get();
    }
}
