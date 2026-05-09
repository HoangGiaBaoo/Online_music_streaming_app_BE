package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.entity.Artist;
import com.huce.online_music_streaming_app.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    public Artist findById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found: " + id));
    }

    public List<Artist> search(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name);
    }

    public Artist save(Artist artist) {
        return artistRepository.save(artist);
    }

    public void deleteById(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new RuntimeException("Artist not found: " + id);
        }
        artistRepository.deleteById(id);
    }
}
