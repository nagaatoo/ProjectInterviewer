package ru.numbdev.interviewer.service.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.jpa.criteria.QuestionSpecification;
import ru.numbdev.interviewer.jpa.entity.QuestionnaireEntity;
import ru.numbdev.interviewer.jpa.repository.QuestionnaireRepository;
import ru.numbdev.interviewer.utils.SecurityUtil;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionsCrudService {

    private final QuestionnaireRepository questionnaireRepository;

    public Page<QuestionnaireEntity> findQuestionary(int page, int pageSize, String quick) {
        return questionnaireRepository.findAll(
                QuestionSpecification.getQuestionnaires(quick, SecurityUtil.getUserName()),
                PageRequest.of(page, pageSize)
        );
    }

    public long getSize(int page, int pageSize, String quick) {
        return questionnaireRepository.findAll(
                QuestionSpecification.getQuestionnaires(quick, SecurityUtil.getUserName()),
                PageRequest.of(page, pageSize)
        ).getTotalElements();
    }

    public QuestionnaireEntity save(QuestionnaireEntity entity) {
        return questionnaireRepository.save(entity);
    }

    public QuestionnaireEntity findById(UUID id) {
        return questionnaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Опросник с id {0} не найден", id)));

    }

    public void delete(QuestionnaireEntity selectedElement) {
        questionnaireRepository.deleteById(selectedElement.getId());
    }

    public List<QuestionnaireEntity> getAvailableQuestions(String userName) {
        return questionnaireRepository.findAvailableQuestions(userName);
    }
}
