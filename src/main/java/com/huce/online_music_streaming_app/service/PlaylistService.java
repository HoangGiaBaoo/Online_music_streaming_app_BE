package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.dto.PlaylistResponse;
import com.huce.online_music_streaming_app.dto.PlaylistUpdateRequest;
import com.huce.online_music_streaming_app.entity.*;
import com.huce.online_music_streaming_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private static final int MAX_NAME_LENGTH = 100;

    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    public List<Playlist> findByUser(Long userId) {
        return playlistRepository.findByUser_UserIdAndIsCuratedFalse(userId);
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
    public PlaylistResponse update(Long playlistId, PlaylistUpdateRequest req, Long currentUserId) {
        Playlist playlist = findById(playlistId);
        assertCanEdit(playlist, currentUserId);

        String rawName = req.name();
        if (rawName == null || rawName.isBlank()) {
            throw new IllegalArgumentException("name_required");
        }
        String trimmed = rawName.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("name_too_long");
        }

        playlist.setName(trimmed);
        if (req.isPublic() != null) {
            playlist.setIsPublic(req.isPublic());
        }
        return PlaylistResponse.from(playlistRepository.save(playlist));
    }

    @Transactional
    public void delete(Long playlistId, Long currentUserId) {
        Playlist playlist = findById(playlistId);
        assertCanEdit(playlist, currentUserId);
        playlistTrackRepository.deleteByIdPlaylistId(playlistId);
        playlistRepository.delete(playlist);
    }

    @Transactional
    public PlaylistResponse updateCover(Long playlistId, String coverUrl, Long currentUserId) {
        Playlist playlist = findById(playlistId);
        assertCanEdit(playlist, currentUserId);
        playlist.setCoverUrl(coverUrl);
        return PlaylistResponse.from(playlistRepository.save(playlist));
    }

    public Playlist findEditable(Long playlistId, Long currentUserId) {
        Playlist playlist = findById(playlistId);
        assertCanEdit(playlist, currentUserId);
        return playlist;
    }

    @Transactional
    public void addTrack(Long playlistId, Long trackId, Long currentUserId) {
        Playlist playlist = findById(playlistId);
        assertOwner(playlist, currentUserId);
        if (Boolean.TRUE.equals(playlist.getIsCurated())) {
            throw new RuntimeException("Cannot modify a curated playlist");
        }
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
    public void removeTrack(Long playlistId, Long trackId, Long currentUserId) {
        Playlist playlist = findById(playlistId);
        assertOwner(playlist, currentUserId);
        if (Boolean.TRUE.equals(playlist.getIsCurated())) {
            throw new RuntimeException("Cannot modify a curated playlist");
        }
        playlistTrackRepository.deleteByIdPlaylistIdAndIdTrackId(playlistId, trackId);
    }

    @Transactional(readOnly = true)
    public List<Track> getTracks(Long playlistId) {
        return playlistTrackRepository.findByIdPlaylistIdOrderByPosition(playlistId)
                .stream()
                .map(PlaylistTrack::getTrack)
                .toList();
    }

    private void assertOwner(Playlist playlist, Long currentUserId) {
        Long ownerId = playlist.getUser() != null ? playlist.getUser().getUserId() : null;
        if (ownerId == null || !ownerId.equals(currentUserId)) {
            throw new RuntimeException("You don't own this playlist");
        }
    }

    private void assertCanEdit(Playlist playlist, Long currentUserId) {
        if (Boolean.TRUE.equals(playlist.getIsCurated())) {
            throw new AccessDeniedException("curated_playlist_readonly");
        }
        Long ownerId = playlist.getUser() != null ? playlist.getUser().getUserId() : null;
        if (ownerId == null || !ownerId.equals(currentUserId)) {
            throw new AccessDeniedException("not_owner");
        }
    }
}
