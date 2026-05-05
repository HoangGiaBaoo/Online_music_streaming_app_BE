package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.entity.*;
import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;
    private final LibraryService libraryService;
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(trackService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(trackService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<?> getRelated(@PathVariable Long id,
                                        @RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(recommendationService.relatedTracks(id, limit));
    }

    @GetMapping("/liked")
    public ResponseEntity<?> getLiked(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        List<LikedTrack> liked = libraryService.getLikedTracks(userId);
        return ResponseEntity.ok(liked.stream().map(LikedTrack::getTrack).toList());
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            libraryService.toggleLike(getUserId(userDetails), id);
            return ResponseEntity.ok(Map.of("message", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getUserId();
    }
}
