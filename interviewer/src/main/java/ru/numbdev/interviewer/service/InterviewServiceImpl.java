package ru.numbdev.interviewer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.jpa.entity.InterviewEntity;
import ru.numbdev.interviewer.jpa.entity.RoomEntity;
import ru.numbdev.interviewer.service.crud.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewCrudService interviewCrudService;
    private final RoomCrudService roomCrudService;
    private final QuestionsCrudService questionsCrudService;
    private final TemplateCrudService templateCrudService;
    private final HistoryService historyService;

    @Transactional
    public String createInterview(
            String name,
            String interviewer,
            String hr,
            LocalDateTime date,
            UUID templateId,
            UUID questionnaireId
    ) {
        var room = roomCrudService.save(new RoomEntity());
        var interview = new InterviewEntity()
                .setName(name)
                .setStarted(false)
                .setInterviewerLogin(interviewer)
                .setHrLogin(hr)
                .setDate(date)
                .setTemplate(
                        templateId != null
                                ? templateCrudService.findById(templateId)
                                : null
                )
                .setQuestionnaire(
                        questionnaireId != null
                                ? questionsCrudService.findById(questionnaireId)
                                : null
                )
                .setRoom(room);
        interviewCrudService.save(interview);

        return room.getId().toString();
    }

    @Transactional
    public void finishInterview(UUID interviewId, List<ElementValues> values) {
        var entity = interviewCrudService.getById(interviewId);
        historyService.saveInterviewResult(entity, values);
        interviewCrudService.save(
                entity
                        .setFinishedDate(LocalDateTime.now())
                        .setStarted(false)
        );
    }

    @Transactional
    public void startInterview(UUID id) {
        var interview = interviewCrudService.getById(id);
        interviewCrudService.save(interview.setStarted(true));
    }

    @Transactional
    public void saveResultReview(UUID interviewId, String result, List<ElementValues> questionValues) {
        var interviewEntity = interviewCrudService.getById(interviewId);
        interviewEntity.setSolution(result);

        historyService.saveAnswersForQuestions(interviewEntity, questionValues);
        interviewCrudService.save(interviewEntity);
    }
}
