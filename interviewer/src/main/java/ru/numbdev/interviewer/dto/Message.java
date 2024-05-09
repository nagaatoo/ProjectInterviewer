package ru.numbdev.interviewer.dto;

import ru.numbdev.interviewer.enums.EventType;

import java.util.Map;
import java.util.UUID;

public record Message(
        UUID roomId,
        EventType event,
        ElementValues value,
        Map<Integer, String> diffs
) {
}
