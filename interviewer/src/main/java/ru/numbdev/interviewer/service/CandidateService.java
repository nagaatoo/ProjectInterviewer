package ru.numbdev.interviewer.service;

import ru.numbdev.interviewer.enums.CandidateSolution;

import java.util.UUID;

public interface CandidateService {
    void createCandidate(String fio, String description, String fileName, byte[] file);
    void updateCandidate(UUID id, String fio, String description, CandidateSolution solution, String fileName, byte[] file);
}
