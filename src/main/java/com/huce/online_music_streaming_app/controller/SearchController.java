package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final TrackService trackService;
    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<?> search(@RequestParam String q) {
        return ResponseEntity.ok(Map.of(
                "tracks", trackService.search(q),
                "artists", artistService.search(q)
        ));
    }
}
