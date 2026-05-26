package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.dto.GenreRequest;
import com.huce.online_music_streaming_app.dto.GenreResponse;
import com.huce.online_music_streaming_app.entity.Genre;
import com.huce.online_music_streaming_app.service.FileStorageService;
import com.huce.online_music_streaming_app.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * GenreAdminController — CRUD Genre cho trang admin + upload ảnh cover.
 * Bảo vệ bởi /api/admin/** trong SecurityConfig (chỉ ROLE_ADMIN truy cập).
 */
@RestController
@RequestMapping("/api/admin/genres")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class GenreAdminController {

    private final GenreService genreService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<?> list() {
        List<GenreResponse> body = genreService.findAll().stream()
                .map(GenreResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            Genre genre = genreService.findById(id);
            return ResponseEntity.ok(GenreResponse.from(genre));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GenreRequest request) {
        try {
            Genre saved = genreService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GenreResponse.from(saved));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Genre đã tồn tại: " + request.name()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GenreRequest request) {
        try {
            Genre updated = genreService.update(id, request);
            return ResponseEntity.ok(GenreResponse.from(updated));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Genre đã tồn tại: " + request.name()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            genreService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/{id}/cover", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCover(@PathVariable Long id,
                                        @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "File must be an image"));
            }
            String coverUrl = fileStorageService.storeImage(file);
            genreService.updateCoverUrl(id, coverUrl);
            return ResponseEntity.ok(Map.of("coverUrl", coverUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
