package ru.numbdev.interviewer.service.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.jpa.entity.FileEntity;
import ru.numbdev.interviewer.jpa.repository.FileRepository;

@Service
@RequiredArgsConstructor
public class FileCrudService {

    private final FileRepository fileRepository;

    public FileEntity save(FileEntity file) {
        return fileRepository.save(file);
    }

}
