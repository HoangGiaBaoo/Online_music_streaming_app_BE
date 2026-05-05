package com.huce.online_music_streaming_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path audioPath = Paths.get(uploadDir, "audio");
        Path imagePath = Paths.get(uploadDir, "images");

        registry.addResourceHandler("/audio/**")
                .addResourceLocations("file:" + audioPath.toAbsolutePath() + "/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath.toAbsolutePath() + "/");
    }
}
