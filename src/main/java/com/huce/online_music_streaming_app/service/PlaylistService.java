package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.entity.*;
import com.huce.online_music_streaming_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    public List<Playlist> findByUser(Long userId) {
        return playlistRepository.findByUser_UserId(userId);
    }

    public Playlist findById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found: " + id));
    }

    public Playlist create(String name, Boolean isPublic, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Playlist playlist = Playlist.builder()
                .name(name)
                .isPublic(isPublic)
                .user(user)
                .build();
        return playlistRepository.save(playlist);
    }

    @Transactional
    public void addTrack(Long playlistId, Long trackId) {
        Playlist playlist = findById(playlistId);
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found: " + trackId));

        int position = playlistTrackRepository.countByIdPlaylistId(playlistId) + 1;
        PlaylistTrack pt = PlaylistTrack.builder()
                .id(new PlaylistTrackId(playlistId, trackId))
                .playlist(playlist)
                .track(track)
                .position(position)
                .build();
        playlistTrackRepository.save(pt);
    }

    @Transactional
    public void removeTrack(Long playlistId, Long trackId) {
        playlistTrackRepository.deleteByIdPlaylistIdAndIdTrackId(playlistId, trackId);
    }

    public List<PlaylistTrack> getTracks(Long playlistId) {
        return playlistTrackRepository.findByIdPlaylistIdOrderByPosition(playlistId);
    }
}
