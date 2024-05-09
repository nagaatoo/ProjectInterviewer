package ru.numbdev.interviewer.service;

import ru.numbDev.common.dto.ElementValues;
import ru.numbDev.common.enums.EventType;
import ru.numbdev.interviewer.component.RoomObserver;

import java.util.Map;
import java.util.UUID;

public interface GlobalCacheService {

    Map<Integer, ElementValues> offerInterview(UUID interviewId, RoomObserver room);
    void endInterview(UUID interviewId, UUID roomId);
    void offerEvent(UUID interviewId, UUID roomId, EventType type);
    void offerComponent(UUID interviewId, UUID roomId, ElementValues value, boolean isChange);
    void offerDiff(UUID interviewId, UUID roomId, UUID elementId, Map<Integer, String> diffs);

}
