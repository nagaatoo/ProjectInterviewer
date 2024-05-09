package ru.numbdev.interviewer.jpa.criteria;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ru.numbdev.interviewer.jpa.entity.TemplateEntity;

import java.util.ArrayList;
import java.util.List;

public class TemplateSpecification {

    public static Specification<TemplateEntity> getTemplates(String quickSearch, String owner) {
        return (template, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(quickSearch)) {
                predicates.add(cb.like(cb.lower(template.get("name")), "%" + quickSearch.toLowerCase() + "%"));
            }

            if (StringUtils.isNotBlank(owner)) {
                predicates.add(cb.equal(template.get("owner"), owner));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
