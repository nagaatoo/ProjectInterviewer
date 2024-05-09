package ru.numbdev.interviewer.jpa.criteria;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ru.numbdev.interviewer.jpa.entity.QuestionnaireEntity;

import java.util.ArrayList;
import java.util.List;

public class QuestionSpecification {
    public static Specification<QuestionnaireEntity> getQuestionnaires(String quickSearch, String owner) {
        return (question, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(quickSearch)) {
                var namePredicate = cb.like(cb.lower(question.get("name")), "%" + quickSearch.toLowerCase() + "%");
                var descriptionPredicate = cb.like(cb.lower(question.get("description")), "%" + quickSearch.toLowerCase() + "%");
                predicates.add(cb.or(namePredicate, descriptionPredicate));
            }

            if (StringUtils.isNotBlank(owner)) {
                predicates.add(cb.equal(question.get("author"), owner));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
