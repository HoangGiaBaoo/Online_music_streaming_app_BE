package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.repository.AlbumRepository;
import com.huce.online_music_streaming_app.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumRepository albumRepository;
    private final TrackService trackService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(albumService.findAll());
    }

    @GetMapping("/new")
    public ResponseEntity<?> newReleases(@RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(albumRepository
                .findAllByOrderByReleaseDateDesc(PageRequest.of(0, limit)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(albumService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/tracks")
    public ResponseEntity<?> getTracks(@PathVariable Long id) {
        return ResponseEntity.ok(trackService.findByAlbum(id));
    }
}
