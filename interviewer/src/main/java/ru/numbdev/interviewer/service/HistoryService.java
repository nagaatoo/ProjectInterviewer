package ru.numbdev.interviewer.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.jpa.entity.InterviewEntity;

public interface HistoryService {

    void saveInterviewResult(InterviewEntity interview, List<ElementValues> values);
    Map<Integer, ElementValues> getInterviewResult(UUID interviewId);
    void saveAnswersForQuestions(InterviewEntity interviewEntity, List<ElementValues> values);
}
