package ru.numbDev.common.dto;

import java.time.LocalDateTime;

import ru.numbDev.common.enums.ElementType;

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
