package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.dto.UserSettingsDto;
import com.huce.online_music_streaming_app.service.UserService;
import com.huce.online_music_streaming_app.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> get(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(userSettingsService.getOrCreate(userId));
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UserSettingsDto dto,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(userSettingsService.update(userId, dto));
    }
}
