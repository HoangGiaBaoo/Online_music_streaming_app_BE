package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.Track;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByAlbum_AlbumId(Long albumId);
    List<Track> findByArtist_ArtistId(Long artistId);
    List<Track> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT t FROM Track t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(t.artist.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Track> search(@Param("q") String query);

    List<Track> findAllByOrderByPlayCountDesc(Pageable pageable);

    List<Track> findByArtist_ArtistIdOrderByPlayCountDesc(Long artistId, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Track t JOIN t.genres g " +
           "WHERE g IN (SELECT g2 FROM Track t2 JOIN t2.genres g2 WHERE t2.trackId = :trackId) " +
           "AND t.trackId <> :trackId")
    List<Track> findRelatedByGenre(@Param("trackId") Long trackId, Pageable pageable);

    @Query("SELECT t FROM Track t JOIN t.genres g WHERE g.genreId = :genreId")
    List<Track> findByGenreId(@Param("genreId") Long genreId);
}
