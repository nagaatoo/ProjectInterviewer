package ru.numbdev.interviewer.jpa.criteria;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ru.numbdev.interviewer.jpa.entity.CandidateEntity;

public class CandidateSpecification {

    public static Specification<CandidateEntity> getCandidates(String quick) {
        return (interview, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(quick)) {
                var namePredicate = cb.like(cb.lower(interview.get("fio")), "%" + quick.toLowerCase() + "%");
                var solutionPredicate = cb.like(cb.lower(interview.get("description")), "%" + quick.toLowerCase() + "%");
                predicates.add(cb.or(namePredicate, solutionPredicate));
            }

            cq.orderBy(List.of(cb.desc(interview.get("created"))));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<CandidateEntity> getCandidateByName(String search) {
        return (interview, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(search)) {
                predicates.add(cb.like(cb.lower(interview.get("fio")), "%" + search.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
