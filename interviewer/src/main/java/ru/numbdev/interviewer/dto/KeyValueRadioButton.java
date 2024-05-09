package ru.numbdev.interviewer.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class KeyValueRadioButton {
    private String selected;
    private List<String> values;
}
