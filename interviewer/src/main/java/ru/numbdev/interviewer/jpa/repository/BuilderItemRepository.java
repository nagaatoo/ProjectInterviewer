package ru.numbdev.interviewer.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.numbdev.interviewer.jpa.entity.BuilderItemEntity;

import java.util.List;
import java.util.UUID;

public interface BuilderItemRepository extends JpaRepository<BuilderItemEntity, UUID> {

    @Query("select bi from BuilderItemEntity bi join fetch bi.template t where t.id = :id")
    List<BuilderItemEntity> findItemsForTemplate(@Param("id") UUID id);

    @Query("select bi from BuilderItemEntity bi join fetch bi.questionnaire t where t.id = :id")
    List<BuilderItemEntity> findItemsForQuestionnaire(@Param("id") UUID id);

    @Modifying
    void deleteAllByIdIn(List<UUID> id);

    @Modifying
    @Transactional
    void deleteById(UUID id);
}
