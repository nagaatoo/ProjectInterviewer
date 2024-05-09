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
import ru.numbdev.interviewer.jpa.entity.TemplateEntity;

import java.util.List;
import java.util.UUID;

public interface TemplateRepository extends PagingAndSortingRepository<TemplateEntity, UUID>, CrudRepository<TemplateEntity, UUID> {
    @Query("select t from TemplateEntity t where t.owner = :owner")
    List<TemplateEntity> findAvailableTemplates(@Param("owner") String owner);

    Page<TemplateEntity> findAll(Specification<TemplateEntity> specification, Pageable pageable);

    @Transactional
    @Modifying
    void deleteById(UUID id);
}
