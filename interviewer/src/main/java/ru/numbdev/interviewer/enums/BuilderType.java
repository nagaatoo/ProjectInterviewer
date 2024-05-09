package ru.numbdev.interviewer.enums;

import lombok.Getter;

public enum BuilderType {
    QUESTIONNAIRE("Вопрос"),
    TEMPLATE("Задачу");

    @Getter
    private final String name;

    BuilderType(String name) {
        this.name = name;
    }

}
