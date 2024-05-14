package ru.numbdev.interviewer.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum CandidateSolution {
    OK("К найму"),
    QUESTION("Есть вопросы"),
    BAD("Отказ");

    @Getter
    private final String text;

    CandidateSolution(String text) {
        this.text = text;
    }

    public static List<String> getSolutionNames() {
        return Arrays.stream(values()).map(CandidateSolution::getText).toList();
    }

    public static CandidateSolution getSolution(String text) {
        return Arrays.stream(values()).filter(v -> v.getText().equals(text)).findFirst().orElse(null);
    }
}
