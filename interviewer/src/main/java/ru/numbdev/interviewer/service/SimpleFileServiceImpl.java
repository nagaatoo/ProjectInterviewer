package ru.numbdev.interviewer.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimpleFileServiceImpl implements FileService {

    @Override
    public String upload(String fileName, byte[] file) {
        System.out.println("Upload file: " + fileName);

        return UUID.randomUUID().toString();
    }

    @Override
    public byte[] download(String link) {
        return new byte[0];
    }

}
