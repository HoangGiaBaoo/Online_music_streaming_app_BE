package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @GetMapping("/daily")
    public ResponseEntity<?> daily(@RequestParam(defaultValue = "10") Integer limit,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow().getUserId();
        return ResponseEntity.ok(recommendationService.dailyMix(userId, limit));
    }
}
