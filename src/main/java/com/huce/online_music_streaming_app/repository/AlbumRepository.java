package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtist_ArtistId(Long artistId);
    List<Album> findByTitleContainingIgnoreCase(String title);
    List<Album> findAllByOrderByReleaseDateDesc(Pageable pageable);

    @Query("SELECT DISTINCT a FROM Album a JOIN Track t ON t.album = a JOIN t.genres g " +
           "WHERE g.genreId = :genreId ORDER BY a.releaseDate DESC")
    List<Album> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);
}
