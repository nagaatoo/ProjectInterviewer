package ru.numbdev.interviewer.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.jpa.entity.HistoryBuilderItemEntity;
import ru.numbdev.interviewer.jpa.entity.InterviewEntity;
import ru.numbdev.interviewer.service.crud.HistoryItemCrudService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryItemCrudService historyItemCrudService;

    @Transactional
    public void saveInterviewResult(InterviewEntity interview, List<ElementValues> values) {
        int count = 0;
        for (var value : values) {
            historyItemCrudService.save(toHistoryEntity(interview, value, count));
            count += 1;
        }
    }

    public Map<Integer, ElementValues> getInterviewResult(UUID interviewId) {
        return historyItemCrudService
                .findByInterviewerId(interviewId)
                .stream()
                .collect(
                        Collectors.toMap(
                                HistoryBuilderItemEntity::getElementOrder,
                                this::entityToDto
                        )
                );
    }

    private ElementValues entityToDto(HistoryBuilderItemEntity entity) {
        return new ElementValues(
                entity.getId().toString(),
                entity.getElementType(),
                entity.getElementDescription(),
                entity.getElementValue()
        );
    }

    private HistoryBuilderItemEntity toHistoryEntity(InterviewEntity interview, ElementValues v, int count) {
        return new HistoryBuilderItemEntity()
                .setId(StringUtils.isNotBlank(v.id()) ? UUID.fromString(v.id()) : null)
                .setInterview(interview)
                .setElementOrder(count)
                .setElementType(v.type())
                .setElementValue(v.value())
                .setElementDescription(v.description());
    }

    @Transactional
    public void saveAnswersForQuestions(InterviewEntity interviewEntity, List<ElementValues> values) {
        int count = 0;
        for (var value : values) {
            var history = historyItemCrudService.save(toHistoryEntity(interviewEntity, value, count));
            history.setQuestionnaire(interviewEntity.getQuestionnaire());
            historyItemCrudService.save(history);
            count += 1;
        }
    }

}
