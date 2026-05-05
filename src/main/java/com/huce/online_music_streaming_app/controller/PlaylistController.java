package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.dto.PlaylistRequest;
import com.huce.online_music_streaming_app.entity.Mood;
import com.huce.online_music_streaming_app.repository.PlaylistRepository;
import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getMyPlaylists(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(playlistService.findByUser(userId));
    }

    @GetMapping("/curated")
    public ResponseEntity<?> getCurated(@RequestParam(required = false) Mood mood) {
        return ResponseEntity.ok(mood == null
                ? playlistRepository.findByIsCuratedTrue()
                : playlistRepository.findByIsCuratedTrueAndMood(mood));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PlaylistRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(playlistService.create(request.getName(), request.getIsPublic(), userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(playlistService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/tracks")
    public ResponseEntity<?> getTracks(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(playlistService.getTracks(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/tracks")
    public ResponseEntity<?> addTrack(@PathVariable Long id,
                                      @RequestParam Long trackId) {
        try {
            playlistService.addTrack(id, trackId);
            return ResponseEntity.ok(Map.of("message", "Track added"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<?> removeTrack(@PathVariable Long id,
                                         @PathVariable Long trackId) {
        playlistService.removeTrack(id, trackId);
        return ResponseEntity.ok(Map.of("message", "Track removed"));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getUserId();
    }
}
