package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.Mood;
import com.huce.online_music_streaming_app.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUser_UserId(Long userId);
    List<Playlist> findByIsCuratedTrue();
    List<Playlist> findByIsCuratedTrueAndMood(Mood mood);
}
