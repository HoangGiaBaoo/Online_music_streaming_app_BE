package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.PlayHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    List<PlayHistory> findByUser_UserIdOrderByPlayedAtDesc(Long userId);
    List<PlayHistory> findByUser_UserIdOrderByPlayedAtDesc(Long userId, Pageable pageable);

    @Query("""
           SELECT t.artist.artistId, t.artist.name, t.artist.avatarUrl, COUNT(ph)
           FROM PlayHistory ph JOIN ph.track t
           WHERE ph.user.userId = :userId
             AND ph.playedAt >= :start AND ph.playedAt < :end
           GROUP BY t.artist.artistId, t.artist.name, t.artist.avatarUrl
           ORDER BY COUNT(ph) DESC
           """)
    List<Object[]> topArtistsInRange(@Param("userId") Long userId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     Pageable pageable);

    @Query("""
           SELECT t.trackId, t.title, t.coverUrl, t.artist.name, COUNT(ph)
           FROM PlayHistory ph JOIN ph.track t
           WHERE ph.user.userId = :userId
             AND ph.playedAt >= :start AND ph.playedAt < :end
           GROUP BY t.trackId, t.title, t.coverUrl, t.artist.name
           ORDER BY COUNT(ph) DESC
           """)
    List<Object[]> topTracksInRange(@Param("userId") Long userId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    Pageable pageable);

    @Query("""
           SELECT COUNT(ph), COALESCE(SUM(t.duration), 0)
           FROM PlayHistory ph JOIN ph.track t
           WHERE ph.user.userId = :userId
             AND ph.playedAt >= :start AND ph.playedAt < :end
           """)
    Object[] aggregateInRange(@Param("userId") Long userId,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);
}
