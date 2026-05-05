package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.entity.Artist;
import com.huce.online_music_streaming_app.entity.FollowedArtist;
import com.huce.online_music_streaming_app.repository.ArtistRepository;
import com.huce.online_music_streaming_app.repository.TrackRepository;
import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;
    private final ArtistRepository artistRepository;
    private final AlbumService albumService;
    private final LibraryService libraryService;
    private final RecommendationService recommendationService;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(artistService.findAll());
    }

    @GetMapping("/popular")
    public ResponseEntity<?> popular(@RequestParam(defaultValue = "10") Integer limit) {
        List<Artist> artists = artistRepository
                .findPopularWithFollowers(PageRequest.of(0, limit))
                .stream().map(row -> (Artist) row[0]).toList();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/followed")
    public ResponseEntity<?> followed(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        List<Artist> artists = libraryService.getFollowedArtists(userId)
                .stream().map(FollowedArtist::getArtist).toList();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(artistService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<?> getAlbums(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.findByArtist(id));
    }

    @GetMapping("/{id}/tracks/popular")
    public ResponseEntity<?> getPopularTracks(@PathVariable Long id,
                                              @RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(trackRepository
                .findByArtist_ArtistIdOrderByPlayCountDesc(id, PageRequest.of(0, limit)));
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<?> getRelated(@PathVariable Long id,
                                        @RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(recommendationService.relatedArtists(id, limit));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> toggleFollow(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            libraryService.toggleFollow(getUserId(userDetails), id);
            return ResponseEntity.ok(Map.of("message", "OK"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow().getUserId();
    }
}
