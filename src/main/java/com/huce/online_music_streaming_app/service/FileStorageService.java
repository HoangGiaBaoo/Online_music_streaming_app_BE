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

    public String storePlaylistCover(MultipartFile file, Long playlistId) {
        try {
            Path dir = Paths.get(uploadDir, "images");
            Files.createDirectories(dir);

            String ext = extractImageExtension(file.getOriginalFilename(), file.getContentType());
            String filename = "playlist_" + playlistId + "_" + System.currentTimeMillis() + ext;
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
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

    private String extractImageExtension(String originalFilename, String contentType) {
        if (originalFilename != null) {
            int dot = originalFilename.lastIndexOf('.');
            if (dot >= 0 && dot < originalFilename.length() - 1) {
                String ext = originalFilename.substring(dot).toLowerCase();
                if (ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png")) {
                    return ext;
                }
            }
        }
        return "image/png".equalsIgnoreCase(contentType) ? ".png" : ".jpg";
    }
}
