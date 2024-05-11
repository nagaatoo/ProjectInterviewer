package ru.numbdev.interviewer.service;

public interface FileService {
    String upload(String fileName, byte[] file);
    byte[] download(String link);
}
