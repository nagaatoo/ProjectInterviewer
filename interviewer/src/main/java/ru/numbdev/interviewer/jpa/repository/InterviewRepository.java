package ru.numbdev.interviewer.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.numbdev.interviewer.jpa.entity.InterviewEntity;

import java.util.UUID;

public interface InterviewRepository extends PagingAndSortingRepository<InterviewEntity, UUID>, CrudRepository<InterviewEntity, UUID> {
    Page<InterviewEntity> findAll(Specification<InterviewEntity> interview, Pageable pageable);
}
