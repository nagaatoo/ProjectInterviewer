package ru.numbdev.interviewer.dto;

import java.time.LocalDateTime;

import ru.numbdev.interviewer.jpa.entity.ElementType;

public record ElementValues(
        String id,
        ElementType type,
        String description,
        String value,
        LocalDateTime created
) {
    public ElementValues(
            String id,
            ElementType type,
            String description,
            String value
    ) {
        this(id, type, description, value, null);
    }
}
