package ru.numbdev.interviewer.service.crud;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.jpa.entity.BuilderItemEntity;
import ru.numbdev.interviewer.jpa.repository.BuilderItemRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuilderItemCrudService {

    private final BuilderItemRepository builderItemRepository;

    public BuilderItemEntity getById(UUID id) {
        return builderItemRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Builder Item with id " + id + " not found"));
    }

    public List<BuilderItemEntity> getItemsForQuestionnaire(UUID questionnaireId) {
        return builderItemRepository.findItemsForQuestionnaire(questionnaireId);
    }

    public List<BuilderItemEntity> getItemsForTemplate(UUID templateId) {
        return builderItemRepository.findItemsForTemplate(templateId);
    }

    public BuilderItemEntity save(BuilderItemEntity entity) {
        return builderItemRepository.save(entity);
    }

    public void delete(BuilderItemEntity entity) {
        builderItemRepository.deleteById(entity.getId());
    }

    public void deleteById(UUID id) {
        builderItemRepository.deleteById(id);
    }

    public void saveItems(List<BuilderItemEntity> items) {
        builderItemRepository.saveAll(items);
    }

    public void deleteItem(List<BuilderItemEntity> items) {
        builderItemRepository.deleteAllByIdIn(items.stream().map(BuilderItemEntity::getId).toList());
    }
}
