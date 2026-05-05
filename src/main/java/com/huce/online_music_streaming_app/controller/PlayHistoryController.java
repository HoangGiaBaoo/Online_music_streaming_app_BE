package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.dto.RecentItemDto;
import com.huce.online_music_streaming_app.repository.PlayHistoryRepository;
import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class PlayHistoryController {

    private final PlayHistoryService playHistoryService;
    private final PlayHistoryRepository playHistoryRepository;
    private final TrackService trackService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(playHistoryService.getHistory(userId));
    }

    @GetMapping("/recent")
    public ResponseEntity<?> recent(@RequestParam(defaultValue = "10") Integer limit,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        Set<Long> seen = new HashSet<>();
        List<RecentItemDto> items = playHistoryRepository
                .findByUser_UserIdOrderByPlayedAtDesc(userId, PageRequest.of(0, limit * 4))
                .stream()
                .filter(h -> seen.add(h.getTrack().getTrackId()))
                .limit(limit)
                .map(h -> new RecentItemDto(h.getTrack(), h.getPlayedAt()))
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<?> record(@RequestParam Long trackId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = getUserId(userDetails);
            playHistoryService.record(userId, trackId);
            trackService.incrementPlayCount(trackId);
            return ResponseEntity.ok(Map.of("message", "Recorded"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getUserId();
    }
}
