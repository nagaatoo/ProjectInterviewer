package ru.numbdev.interviewer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import ru.numbdev.interviewer.dto.ElementValues;

public interface InterviewService {

    String createInterview(
            String name,
            String interviewer,
            String hr,
            LocalDateTime date,
            UUID templateId,
            UUID questionnaireId
    );
    void finishInterview(UUID interviewId, List<ElementValues> values);
    void startInterview(UUID id);
    void saveResultReview(UUID interviewId, String result, List<ElementValues> questionValues);
}
