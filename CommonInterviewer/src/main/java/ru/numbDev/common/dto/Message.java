package ru.numbDev.common.dto;

import java.util.Map;
import java.util.UUID;

import ru.numbDev.common.enums.EventType;

public record Message(
        UUID roomId,
        EventType event,
        ElementValues value,
        Map<Integer, String> diffs
) {
}
