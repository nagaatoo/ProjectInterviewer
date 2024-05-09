package ru.numbdev.interviewer.service;

import ru.numbdev.interviewer.component.RoomObserver;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.enums.EventType;

import java.util.Map;
import java.util.UUID;

public interface GlobalCacheService {

    Map<Integer, ElementValues> offerInterview(UUID interviewId, RoomObserver room);
    void endInterview(UUID interviewId, UUID roomId);
    void offerEvent(UUID interviewId, UUID roomId, EventType type);
    void offerComponent(UUID interviewId, UUID roomId, ElementValues value, boolean isChange);
    void offerDiff(UUID interviewId, UUID roomId, UUID elementId, Map<Integer, String> diffs);

}
