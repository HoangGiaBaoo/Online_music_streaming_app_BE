package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.entity.Album;
import com.huce.online_music_streaming_app.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;

    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    public Album findById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found: " + id));
    }

    public List<Album> findByArtist(Long artistId) {
        return albumRepository.findByArtist_ArtistId(artistId);
    }

    public Album save(Album album) {
        return albumRepository.save(album);
    }
}
