package ru.numbdev.interviewer.jpa.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.numbdev.interviewer.jpa.entity.CandidateEntity;

public interface CandidateRepository extends PagingAndSortingRepository<CandidateEntity, UUID>, CrudRepository<CandidateEntity, UUID> {
    Page<CandidateEntity> findAll(Specification<CandidateEntity> interview, Pageable pageable);
}
