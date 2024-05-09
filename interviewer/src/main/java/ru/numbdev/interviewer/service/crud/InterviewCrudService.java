package ru.numbdev.interviewer.service.crud;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.jpa.criteria.InterviewSpecification;
import ru.numbdev.interviewer.jpa.entity.InterviewEntity;
import ru.numbdev.interviewer.enums.PaginationDirection;
import ru.numbdev.interviewer.jpa.repository.InterviewRepository;
import ru.numbdev.interviewer.utils.SecurityUtil;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewCrudService {

    private final InterviewRepository interviewRepository;

    public InterviewEntity save(InterviewEntity entity) {
        return interviewRepository.save(entity);
    }

    public InterviewEntity getById(UUID id) {
        return interviewRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Interview not found " + id));
    }

    public long getSize(int page, int size, String quickSearch) {
        return interviewRepository.findAll(
                InterviewSpecification.getInterview(quickSearch, SecurityUtil.getUserName()),
                PageRequest.of(
                        page,
                        size
                )
        ).getTotalElements();
    }
    public Page<InterviewEntity> findInterview(int page, int size, String quickSearch) {
        return findInterview(page, size, List.of(), null, quickSearch);
    }

    public Page<InterviewEntity> findInterview(
            int page,
            int size,
            List<String> sortBy,
            PaginationDirection direction,
            String quickSearch
    ) {
        return interviewRepository.findAll(
                InterviewSpecification.getInterview(quickSearch, SecurityUtil.getUserName()),
                PageRequest.of(
                        page,
                        size
                )
        );
    }


}
