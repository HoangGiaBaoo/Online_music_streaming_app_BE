package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.PlaylistTrack;
import com.huce.online_music_streaming_app.entity.PlaylistTrackId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, PlaylistTrackId> {
    List<PlaylistTrack> findByIdPlaylistIdOrderByPosition(Long playlistId);
    void deleteByIdPlaylistIdAndIdTrackId(Long playlistId, Long trackId);
    int countByIdPlaylistId(Long playlistId);
}