package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.service.HomeFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeFeedService homeFeedService;
    private final UserRepository userRepository;

    @GetMapping("/feed")
    public ResponseEntity<?> feed(@RequestParam(required = false) String filter,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow().getUserId();
        return ResponseEntity.ok(homeFeedService.buildFeed(userId, filter));
    }
}
