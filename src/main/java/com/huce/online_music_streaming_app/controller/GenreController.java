package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.repository.TrackRepository;
import com.huce.online_music_streaming_app.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final TrackRepository trackRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(genreService.findAll());
    }

    @GetMapping("/{id}/tracks")
    public ResponseEntity<?> getTracks(@PathVariable Long id) {
        return ResponseEntity.ok(trackRepository.findByGenreId(id));
    }
}
