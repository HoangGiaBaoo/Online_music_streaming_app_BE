package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.dto.PlaylistRequest;
import com.huce.online_music_streaming_app.dto.PlaylistResponse;
import com.huce.online_music_streaming_app.dto.PlaylistUpdateRequest;
import com.huce.online_music_streaming_app.entity.Mood;
import com.huce.online_music_streaming_app.entity.Playlist;
import com.huce.online_music_streaming_app.repository.PlaylistRepository;
import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.service.FileStorageService;
import com.huce.online_music_streaming_app.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private static final long MAX_COVER_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_COVER_TYPES = Set.of("image/jpeg", "image/png");

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

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

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlaylist(@PathVariable Long id,
                                            @RequestBody PlaylistUpdateRequest request,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            PlaylistResponse response = playlistService.update(id, request, getUserId(userDetails));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            playlistService.delete(id, getUserId(userDetails));
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/{id}/cover", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCover(@PathVariable Long id,
                                         @RequestParam("file") MultipartFile file,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long currentUserId = getUserId(userDetails);
            Playlist playlist = playlistService.findEditable(id, currentUserId);

            if (file == null || file.isEmpty()
                    || file.getSize() > MAX_COVER_BYTES
                    || file.getContentType() == null
                    || !ALLOWED_COVER_TYPES.contains(file.getContentType().toLowerCase())) {
                return ResponseEntity.badRequest().body(Map.of("error", "invalid_image"));
            }

            String coverUrl = fileStorageService.storePlaylistCover(file, playlist.getPlaylistId());
            playlistService.updateCover(id, coverUrl, currentUserId);
            return ResponseEntity.ok(Map.of("coverUrl", coverUrl));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
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
                                      @RequestParam Long trackId,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            playlistService.addTrack(id, trackId, getUserId(userDetails));
            return ResponseEntity.ok(Map.of("message", "Track added"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<?> removeTrack(@PathVariable Long id,
                                         @PathVariable Long trackId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            playlistService.removeTrack(id, trackId, getUserId(userDetails));
            return ResponseEntity.ok(Map.of("message", "Track removed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getUserId();
    }
}
