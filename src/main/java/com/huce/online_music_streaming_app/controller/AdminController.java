package com.huce.online_music_streaming_app.controller;

import com.huce.online_music_streaming_app.entity.Album;
import com.huce.online_music_streaming_app.entity.Artist;
import com.huce.online_music_streaming_app.entity.Genre;
import com.huce.online_music_streaming_app.entity.Track;
import com.huce.online_music_streaming_app.repository.AlbumRepository;
import com.huce.online_music_streaming_app.repository.ArtistRepository;
import com.huce.online_music_streaming_app.repository.GenreRepository;
import com.huce.online_music_streaming_app.repository.TrackRepository;
import com.huce.online_music_streaming_app.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AdminController — các endpoint CRUD còn thiếu cho trang admin React.

 * Đặt file này vào package controller của project.
 * Tất cả endpoint yêu cầu role ADMIN (đã cấu hình trong SecurityConfig: /api/admin/**).

 * Base URL: /api/admin
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final GenreRepository genreRepository;
    private final FileStorageService fileStorageService;

    // ─────────────────────────────────────────────────────────────
    // ARTIST
    // ─────────────────────────────────────────────────────────────

    /**
     * POST /api/admin/artists
     * Body: multipart/form-data
     *   name    (required) — tên nghệ sĩ
     *   bio     (optional) — tiểu sử
     *   avatar  (optional) — file ảnh (.jpg/.png)
     */
    @PostMapping(value = "/artists", consumes = "multipart/form-data")
    public ResponseEntity<?> createArtist(
            @RequestParam("name") String name,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            Artist artist = new Artist();
            artist.setName(name);
            artist.setBio(bio);

            if (avatar != null && !avatar.isEmpty()) {
                String avatarUrl = fileStorageService.storeImage(avatar);
                artist.setAvatarUrl(avatarUrl);
            }

            Artist saved = artistRepository.save(artist);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/artists/{id}
     * Body: multipart/form-data (các field muốn update, bỏ trống = giữ nguyên)
     */
    @PutMapping(value = "/artists/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateArtist(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            Artist artist = artistRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Artist not found: " + id));

            if (name != null && !name.isBlank()) artist.setName(name);
            if (bio != null) artist.setBio(bio);
            if (avatar != null && !avatar.isEmpty()) {
                artist.setAvatarUrl(fileStorageService.storeImage(avatar));
            }

            return ResponseEntity.ok(artistRepository.save(artist));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/artists/{id}
     * 204 No Content nếu xoá thành công.
     * 409 Conflict nếu còn album/track liên quan (FK violation).
     */
    @DeleteMapping("/artists/{id}")
    public ResponseEntity<?> deleteArtist(@PathVariable Long id) {
        if (!artistRepository.existsById(id)) return ResponseEntity.notFound().build();
        try {
            artistRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(409).body(Map.of("error",
                    "Không thể xoá nghệ sĩ — còn album hoặc bài hát liên kết"));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ALBUM
    // ─────────────────────────────────────────────────────────────

    /**
     * POST /api/admin/albums
     * Body: multipart/form-data
     *   artistId    (required)
     *   title       (required)
     *   albumType   (required) — ALBUM | SINGLE | EP | COMPILATION
     *   releaseDate (optional) — yyyy-MM-dd
     *   description (optional)
     *   cover       (optional) — file ảnh bìa
     */
    @PostMapping(value = "/albums", consumes = "multipart/form-data")
    public ResponseEntity<?> createAlbum(
            @RequestParam("artistId") Long artistId,
            @RequestParam("title") String title,
            @RequestParam("albumType") String albumType,
            @RequestParam(value = "releaseDate", required = false) String releaseDate,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cover", required = false) MultipartFile cover) {
        try {
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Artist not found: " + artistId));

            Album album = new Album();
            album.setArtist(artist);
            album.setTitle(title);
            album.setAlbumType(com.huce.online_music_streaming_app.entity.AlbumType.valueOf(albumType));
            album.setDescription(description);

            if (releaseDate != null && !releaseDate.isBlank()) {
                album.setReleaseDate(LocalDate.parse(releaseDate));
            }
            if (cover != null && !cover.isEmpty()) {
                album.setCoverUrl(fileStorageService.storeImage(cover));
            }

            return ResponseEntity.ok(albumRepository.save(album));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/albums/{id}
     */
    @PutMapping(value = "/albums/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateAlbum(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "albumType", required = false) String albumType,
            @RequestParam(value = "releaseDate", required = false) String releaseDate,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cover", required = false) MultipartFile cover) {
        try {
            Album album = albumRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Album not found: " + id));

            if (title != null && !title.isBlank()) album.setTitle(title);
            if (albumType != null && !albumType.isBlank()) {
                album.setAlbumType(com.huce.online_music_streaming_app.entity.AlbumType.valueOf(albumType));
            }
            if (releaseDate != null && !releaseDate.isBlank()) {
                album.setReleaseDate(LocalDate.parse(releaseDate));
            }
            if (description != null) album.setDescription(description);
            if (cover != null && !cover.isEmpty()) {
                album.setCoverUrl(fileStorageService.storeImage(cover));
            }

            return ResponseEntity.ok(albumRepository.save(album));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/albums/{id}
     * 204 No Content nếu xoá thành công.
     * 409 Conflict nếu còn track thuộc album này.
     */
    @DeleteMapping("/albums/{id}")
    public ResponseEntity<?> deleteAlbum(@PathVariable Long id) {
        if (!albumRepository.existsById(id)) return ResponseEntity.notFound().build();
        try {
            albumRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(409).body(Map.of("error",
                    "Không thể xoá album — còn bài hát liên kết"));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // TRACK — update metadata + xoá
    // (upload track đã có ở FileController: POST /api/admin/tracks/upload)
    // ─────────────────────────────────────────────────────────────

    /**
     * PUT /api/admin/tracks/{id}
     * Sửa metadata track: title, duration, lyrics, albumId, artistId, cover, genreIds.
     * genreIds: danh sách Long, gửi nhiều lần cùng key (multipart list).
     * albumId rỗng = đổi thành single (album = null).
     */
    @PutMapping(value = "/tracks/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateTrack(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "duration", required = false) Integer duration,
            @RequestParam(value = "lyrics", required = false) String lyrics,
            @RequestParam(value = "albumId", required = false) String albumIdStr,
            @RequestParam(value = "artistId", required = false) Long artistId,
            @RequestParam(value = "genreIds", required = false) List<Long> genreIds,
            @RequestParam(value = "cover", required = false) MultipartFile cover) {
        try {
            Track track = trackRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Track not found: " + id));

            if (title != null && !title.isBlank()) track.setTitle(title);
            if (duration != null) track.setDuration(duration);
            if (lyrics != null) track.setLyrics(lyrics);

            if (artistId != null) {
                Artist artist = artistRepository.findById(artistId)
                        .orElseThrow(() -> new RuntimeException("Artist not found: " + artistId));
                track.setArtist(artist);
            }
            // albumIdStr rỗng = xoá liên kết album (đổi thành single)
            if (albumIdStr != null) {
                if (albumIdStr.isBlank()) {
                    track.setAlbum(null);
                } else {
                    Long albumId = Long.parseLong(albumIdStr);
                    Album album = albumRepository.findById(albumId)
                            .orElseThrow(() -> new RuntimeException("Album not found: " + albumId));
                    track.setAlbum(album);
                }
            }
            if (cover != null && !cover.isEmpty()) {
                track.setCoverUrl(fileStorageService.storeImage(cover));
            }
            if (genreIds != null) {
                List<Genre> genres = genreRepository.findAllById(genreIds);
                track.setGenres(new java.util.HashSet<>(genres));
            }

            return ResponseEntity.ok(trackRepository.save(track));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/tracks/{id}
     * 204 No Content nếu xoá thành công.
     * 409 Conflict nếu track còn trong Playlist_Tracks, Liked_Tracks hoặc Play_History.
     * Lưu ý: Hibernate tự xoá Track_Genres (owning side @ManyToMany) khi xoá Track.
     */
    @DeleteMapping("/tracks/{id}")
    public ResponseEntity<?> deleteTrack(@PathVariable Long id) {
        if (!trackRepository.existsById(id)) return ResponseEntity.notFound().build();
        try {
            trackRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(409).body(Map.of("error",
                    "Không thể xoá bài hát — còn trong playlist hoặc lịch sử nghe"));
        }
    }

}