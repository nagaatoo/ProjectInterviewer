package ru.numbdev.interviewer.service.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.jpa.criteria.TemplateSpecification;
import ru.numbdev.interviewer.jpa.entity.TemplateEntity;
import ru.numbdev.interviewer.jpa.repository.TemplateRepository;
import ru.numbdev.interviewer.utils.SecurityUtil;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TemplateCrudService {

    private final TemplateRepository templateRepository;

    public TemplateEntity findById(UUID id) {
        return templateRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Шаблон с id {0} не найден", id)));

    }

    public TemplateEntity save(TemplateEntity entity) {
        return templateRepository.save(entity);
    }

    public void delete(TemplateEntity entity) {
        templateRepository.deleteById(entity.getId());
    }

    public List<TemplateEntity> getAvailableTemplates(String owner) {
        return templateRepository.findAvailableTemplates(owner);
    }

    public Page<TemplateEntity> findTemplates(int page, int pageSize, String quick) {
        return templateRepository.findAll(
                TemplateSpecification.getTemplates(quick, SecurityUtil.getUserName()),
                PageRequest.of(page, pageSize)
        );
    }

    public long getSize(int page, int pageSize, String quick) {
        return templateRepository.findAll(
                TemplateSpecification.getTemplates(quick, SecurityUtil.getUserName()),
                PageRequest.of(page, pageSize)
        ).getTotalElements();
    }

}
