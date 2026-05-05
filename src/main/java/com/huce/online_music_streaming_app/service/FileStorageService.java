package com.huce.online_music_streaming_app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeAudio(MultipartFile file) {
        return store(file, "audio");
    }

    public String storeImage(MultipartFile file) {
        return store(file, "images");
    }

    private String store(MultipartFile file, String subfolder) {
        try {
            Path dir = Paths.get(uploadDir, subfolder);
            Files.createDirectories(dir);

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/" + subfolder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }
}
