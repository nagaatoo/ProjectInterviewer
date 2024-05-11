package ru.numbdev.interviewer.service;

import java.util.UUID;

public interface CandidateService {
    void createCandidate(String fio, String description, String fileName, byte[] file);
    void updateCandidate(UUID id, String fio, String description, String fileName, byte[] file);
}
