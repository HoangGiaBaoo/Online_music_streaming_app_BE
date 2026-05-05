package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.LikedTrack;
import com.huce.online_music_streaming_app.entity.LikedTrackId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LikedTrackRepository extends JpaRepository<LikedTrack, LikedTrackId> {
    List<LikedTrack> findByIdUserId(Long userId);
    boolean existsByIdUserIdAndIdTrackId(Long userId, Long trackId);
    void deleteByIdUserIdAndIdTrackId(Long userId, Long trackId);
}