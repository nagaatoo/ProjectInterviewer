package ru.numbdev.interviewer.service.crud;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.jpa.entity.HistoryBuilderItemEntity;
import ru.numbdev.interviewer.jpa.repository.HistoryBuilderItemRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryItemCrudService {

    private final HistoryBuilderItemRepository repository;

    public HistoryBuilderItemEntity save(HistoryBuilderItemEntity entity) {
        return repository.save(entity);
    }

    public List<HistoryBuilderItemEntity> findByInterviewerId(UUID interviewerId) {
        return repository.findByInterviewId(interviewerId);
    }

    public List<HistoryBuilderItemEntity> findByInterviewerIdAndQuestionnaireId(UUID interviewId, UUID questionnaireId) {
        return repository.findByInterviewIdAndQuestionnaireId(interviewId, questionnaireId);
    }

    public HistoryBuilderItemEntity getById(UUID historyId) {
        return repository
                .findById(historyId)
                .orElseThrow(() -> new EntityNotFoundException("History item with id " + historyId + " not found"));
    }
}
