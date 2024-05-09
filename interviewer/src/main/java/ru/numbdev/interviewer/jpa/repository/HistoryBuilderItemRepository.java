package ru.numbdev.interviewer.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.numbdev.interviewer.jpa.entity.HistoryBuilderItemEntity;

import java.util.List;
import java.util.UUID;

public interface HistoryBuilderItemRepository extends JpaRepository<HistoryBuilderItemEntity, UUID> {
    @Query("select h from HistoryBuilderItemEntity h join fetch h.interview i where i.id = :interviewId and h.questionnaire is null")
    List<HistoryBuilderItemEntity> findByInterviewId(@Param("interviewId") UUID interviewId);
    List<HistoryBuilderItemEntity> findByInterviewIdAndQuestionnaireId(UUID interviewId, UUID questionnaireId);
}
