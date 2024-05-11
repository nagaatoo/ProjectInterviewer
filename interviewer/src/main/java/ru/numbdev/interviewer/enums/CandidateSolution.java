package ru.numbdev.interviewer.enums;

import lombok.Getter;

public enum CandidateSolution {
    OK("К найму"),
    QUESTION("Есть вопросы"),
    BAD("Отказ");

    @Getter
    private final String text;

    CandidateSolution(String text) {
        this.text = text;
    }
}
