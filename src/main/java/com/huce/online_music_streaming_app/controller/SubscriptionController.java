package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.dto.SubscribeRequest;
import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.getCurrent(getUserId(userDetails)));
    }

    @GetMapping("/plans")
    public ResponseEntity<?> plans() {
        return ResponseEntity.ok(subscriptionService.getPlans());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscribeRequest body,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        if (body.getPlan() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "plan is required"));
        }
        return ResponseEntity.ok(subscriptionService.subscribe(getUserId(userDetails), body.getPlan()));
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancel(@AuthenticationPrincipal UserDetails userDetails) {
        subscriptionService.cancel(getUserId(userDetails));
        return ResponseEntity.ok(Map.of("message", "Cancelled"));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getUserId();
    }
}
