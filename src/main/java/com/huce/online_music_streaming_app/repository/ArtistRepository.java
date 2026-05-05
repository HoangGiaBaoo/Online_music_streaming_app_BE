package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByNameContainingIgnoreCase(String name);

    @Query("SELECT a, COUNT(f) AS followers FROM Artist a " +
           "LEFT JOIN FollowedArtist f ON f.artist = a " +
           "GROUP BY a ORDER BY followers DESC")
    List<Object[]> findPopularWithFollowers(Pageable pageable);

    @Query("SELECT DISTINCT a FROM Artist a JOIN Track t ON t.artist = a JOIN t.genres g " +
           "WHERE g IN (SELECT g2 FROM Track t2 JOIN t2.genres g2 WHERE t2.artist.artistId = :artistId) " +
           "AND a.artistId <> :artistId")
    List<Artist> findRelatedByGenre(@Param("artistId") Long artistId, Pageable pageable);
}
