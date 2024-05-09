package ru.numbdev.interviewer.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.numbdev.interviewer.jpa.entity.QuestionnaireEntity;
import ru.numbdev.interviewer.jpa.entity.TemplateEntity;


import java.util.List;
import java.util.UUID;

public interface QuestionnaireRepository extends PagingAndSortingRepository<QuestionnaireEntity, UUID>, CrudRepository<QuestionnaireEntity, UUID> {
    Page<QuestionnaireEntity> findAll(Specification<QuestionnaireEntity> specification, Pageable pageable);

    @Query("select q from QuestionnaireEntity q where q.author = :owner")
    List<QuestionnaireEntity> findAvailableQuestions(@Param("owner") String owner);

    @Modifying
    @Transactional
    void deleteById(UUID id);
}
