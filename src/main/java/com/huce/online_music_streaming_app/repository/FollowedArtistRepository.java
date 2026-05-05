package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.FollowedArtist;
import com.huce.online_music_streaming_app.entity.FollowedArtistId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FollowedArtistRepository extends JpaRepository<FollowedArtist, FollowedArtistId> {
    List<FollowedArtist> findByIdUserId(Long userId);
    boolean existsByIdUserIdAndIdArtistId(Long userId, Long artistId);
    void deleteByIdUserIdAndIdArtistId(Long userId, Long artistId);
}