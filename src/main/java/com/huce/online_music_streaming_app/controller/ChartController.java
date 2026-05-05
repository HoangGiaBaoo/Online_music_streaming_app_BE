package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.entity.Artist;
import com.huce.online_music_streaming_app.repository.ArtistRepository;
import com.huce.online_music_streaming_app.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class ChartController {

    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;

    @GetMapping("/tracks")
    public ResponseEntity<?> chartTracks(@RequestParam(defaultValue = "50") Integer limit) {
        return ResponseEntity.ok(trackRepository.findAllByOrderByPlayCountDesc(PageRequest.of(0, limit)));
    }

    @GetMapping("/artists")
    public ResponseEntity<?> chartArtists(@RequestParam(defaultValue = "20") Integer limit) {
        List<Artist> artists = artistRepository
                .findPopularWithFollowers(PageRequest.of(0, limit))
                .stream().map(row -> (Artist) row[0]).toList();
        return ResponseEntity.ok(artists);
    }
}
