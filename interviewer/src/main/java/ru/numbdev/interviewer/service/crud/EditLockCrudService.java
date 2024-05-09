package ru.numbdev.interviewer.service.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.jpa.entity.EditLockEntity;
import ru.numbdev.interviewer.jpa.repository.EditLockRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditLockCrudService {

    private final EditLockRepository editLockRepository;

    public EditLockEntity save(EditLockEntity entity) {
        return editLockRepository.save(entity);
    }

    public void delete(UUID id) {
        editLockRepository.deleteById(id);
    }


}
