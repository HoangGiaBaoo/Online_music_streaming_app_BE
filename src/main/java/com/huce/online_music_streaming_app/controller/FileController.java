package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.entity.*;
import com.huce.online_music_streaming_app.repository.*;
import com.huce.online_music_streaming_app.service.FileStorageService;
import com.huce.online_music_streaming_app.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final TrackService trackService;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/api/admin/tracks/upload")
    public ResponseEntity<?> uploadTrack(
            @RequestParam("file") MultipartFile audioFile,
            @RequestParam(value = "cover", required = false) MultipartFile coverFile,
            @RequestParam("title") String title,
            @RequestParam("artistId") Long artistId,
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam(value = "duration", required = false) Integer duration,
            @RequestParam(value = "lyrics", required = false) String lyrics,
            @RequestParam(value = "genreIds", required = false) List<Long> genreIds) {
        try {
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Artist not found"));

            Album album = albumId != null
                    ? albumRepository.findById(albumId).orElse(null)
                    : null;

            String audioUrl = fileStorageService.storeAudio(audioFile);
            String coverUrl = coverFile != null ? fileStorageService.storeImage(coverFile) : null;

            Track track = Track.builder()
                    .title(title)
                    .artist(artist)
                    .album(album)
                    .audioUrl(audioUrl)
                    .coverUrl(coverUrl)
                    .duration(duration)
                    .lyrics(lyrics)
                    .build();

            if (genreIds != null && !genreIds.isEmpty()) {
                track.setGenres(new HashSet<>(genreRepository.findAllById(genreIds)));
            }

            return ResponseEntity.ok(trackService.save(track));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/audio/{filename}")
    public ResponseEntity<byte[]> streamAudio(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

        Path filePath = Paths.get(uploadDir, "audio", filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileBytes = Files.readAllBytes(filePath);
        long fileSize = fileBytes.length;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.set("Accept-Ranges", "bytes");

        if (rangeHeader == null) {
            headers.setContentLength(fileSize);
            return ResponseEntity.ok().headers(headers).body(fileBytes);
        }

        // Parse Range: bytes=start-end
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        long start = Long.parseLong(ranges[0]);
        long end = ranges.length > 1 && !ranges[1].isEmpty()
                ? Long.parseLong(ranges[1])
                : fileSize - 1;

        end = Math.min(end, fileSize - 1);
        long length = end - start + 1;

        byte[] chunk = new byte[(int) length];
        System.arraycopy(fileBytes, (int) start, chunk, 0, (int) length);

        headers.set("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
        headers.setContentLength(length);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).body(chunk);
    }
}
