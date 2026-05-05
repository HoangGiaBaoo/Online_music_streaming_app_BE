package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.service.ListeningStatsService;
import com.huce.online_music_streaming_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ListeningStatsService listeningStatsService;
    private final UserService userService;

    @GetMapping("/listening")
    public ResponseEntity<?> listening(@RequestParam(defaultValue = "week") String period,
                                       @RequestParam(defaultValue = "0") Integer offset,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(listeningStatsService.getStats(userId, period, offset));
    }
}
