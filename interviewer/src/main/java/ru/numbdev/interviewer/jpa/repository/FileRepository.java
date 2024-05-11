package ru.numbdev.interviewer.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.numbdev.interviewer.jpa.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
