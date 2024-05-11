package ru.numbdev.interviewer.service.crud;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.enums.PaginationDirection;
import ru.numbdev.interviewer.jpa.criteria.CandidateSpecification;
import ru.numbdev.interviewer.jpa.entity.CandidateEntity;
import ru.numbdev.interviewer.jpa.repository.CandidateRepository;
import ru.numbdev.interviewer.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
public class CandidateCrudService {

    private final CandidateRepository candidateRepository;

    public CandidateEntity save(CandidateEntity candidateEntity) {
        return candidateRepository.save(candidateEntity);
    }

    public CandidateEntity getById(UUID id) {
        return candidateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Candidate with id " + id + " not found"));
    }

    public long getSize(int page, int size, String quickSearch) {
        return candidateRepository.findAll(
                CandidateSpecification.getCandidates(quickSearch),
                PageRequest.of(
                        page,
                        size
                )
        ).getTotalElements();
    }
    public Page<CandidateEntity> findCandidates(int page, int size, String quickSearch) {
        return findCandidates(page, size, List.of(), null, quickSearch);
    }

    public Page<CandidateEntity> findCandidates(
            int page,
            int size,
            List<String> sortBy,
            PaginationDirection direction,
            String quickSearch
    ) {
        return candidateRepository.findAll(
                CandidateSpecification.getCandidates(quickSearch),
                PageRequest.of(
                        page,
                        size
                )
        );
    }
}
