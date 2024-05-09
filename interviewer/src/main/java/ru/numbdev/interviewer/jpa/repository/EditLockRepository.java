package ru.numbdev.interviewer.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import ru.numbdev.interviewer.jpa.entity.EditLockEntity;

import java.util.UUID;

public interface EditLockRepository extends JpaRepository<EditLockEntity, UUID> {
    @Modifying
    @Transactional
    void deleteById(UUID id);
}
