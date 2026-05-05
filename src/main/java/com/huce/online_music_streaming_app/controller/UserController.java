package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.dto.ProfileUpdateRequest;
import com.huce.online_music_streaming_app.entity.User;
import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.service.FileStorageService;
import com.huce.online_music_streaming_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(userService.getMe(userId));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
            return ResponseEntity.ok(userService.updateProfile(userId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me/profile")
    public ResponseEntity<?> myProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUsername(userDetails.getUsername()).getUserId();
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> profile(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getProfile(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/me/avatar", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "File must be an image"));
            }
            User user = userService.findByUsername(userDetails.getUsername());
            String url = fileStorageService.storeImage(file);
            user.setAvatarUrl(url);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("avatarUrl", url));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
