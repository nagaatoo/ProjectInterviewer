package ru.numbdev.interviewer.jpa.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

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
