package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.entity.Track;
import com.huce.online_music_streaming_app.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;

    public List<Track> findAll() {
        return trackRepository.findAll();
    }

    public Track findById(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found: " + id));
    }

    public List<Track> findByAlbum(Long albumId) {
        return trackRepository.findByAlbum_AlbumId(albumId);
    }

    public List<Track> search(String query) {
        return trackRepository.search(query);
    }

    public Track save(Track track) {
        return trackRepository.save(track);
    }

    public void incrementPlayCount(Long id) {
        Track track = findById(id);
        track.setPlayCount(track.getPlayCount() + 1);
        trackRepository.save(track);
    }
}
