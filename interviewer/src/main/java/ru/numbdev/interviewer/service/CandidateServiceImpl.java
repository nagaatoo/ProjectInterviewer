package ru.numbdev.interviewer.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.numbdev.interviewer.jpa.entity.CandidateEntity;
import ru.numbdev.interviewer.jpa.entity.FileEntity;
import ru.numbdev.interviewer.service.crud.CandidateCrudService;
import ru.numbdev.interviewer.service.crud.FileCrudService;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateCrudService candidateCrudService;
    private final FileCrudService fileCrudService;
    private final FileService fileService;

    @Override
    @Transactional
    public void createCandidate(String fio, String description, String fileName, byte[] file) {
        createOrUpdateCandidate(new CandidateEntity(), fio, description, fileName, file);
    }

    @Override
    @Transactional
    public void updateCandidate(UUID id, String fio, String description, String fileName, byte[] file) {
        createOrUpdateCandidate(candidateCrudService.getById(id), fio, description, fileName, file);
    }

    private void createOrUpdateCandidate(CandidateEntity entity, String fio, String description, String fileName,
                                         byte[] file) {
        entity
                .setFio(fio)
                .setDescription(description)
                .setDeleted(false);

        if (file != null && file.length != 0) {
            var fileEntity = new FileEntity()
                    .setFileName(fileName)
                    .setLink(fileService.upload(fileName, file));

            entity.setFile(fileCrudService.save(fileEntity));
        }

        candidateCrudService.save(entity);
    }
}
