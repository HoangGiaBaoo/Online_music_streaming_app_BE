package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.dto.GenreRequest;
import com.huce.online_music_streaming_app.entity.Genre;
import com.huce.online_music_streaming_app.entity.Track;
import com.huce.online_music_streaming_app.repository.GenreRepository;
import com.huce.online_music_streaming_app.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final TrackRepository trackRepository;

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Genre findById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found: " + id));
    }

    @Transactional
    public Genre create(GenreRequest req) {
        Genre genre = Genre.builder()
                .name(req.name().trim())
                .coverColor(req.coverColor())
                .coverUrl(req.coverUrl())
                .build();
        return genreRepository.save(genre);
    }

    @Transactional
    public Genre update(Long id, GenreRequest req) {
        Genre genre = findById(id);
        genre.setName(req.name().trim());
        genre.setCoverColor(req.coverColor());
        if (req.coverUrl() != null) {
            genre.setCoverUrl(req.coverUrl());
        }
        return genreRepository.save(genre);
    }

    @Transactional
    public Genre updateCoverUrl(Long id, String coverUrl) {
        Genre genre = findById(id);
        genre.setCoverUrl(coverUrl);
        return genreRepository.save(genre);
    }

    /**
     * Cascade-null: gỡ genre khỏi mọi Track tham chiếu trước khi xoá,
     * tránh FK violation trên bảng Track_Genres (Track là owning side).
     */
    @Transactional
    public void delete(Long id) {
        Genre genre = findById(id);
        List<Track> tracks = trackRepository.findByGenreId(id);
        for (Track t : tracks) {
            t.getGenres().remove(genre);
        }
        if (!tracks.isEmpty()) {
            trackRepository.saveAll(tracks);
        }
        genreRepository.delete(genre);
    }
}
