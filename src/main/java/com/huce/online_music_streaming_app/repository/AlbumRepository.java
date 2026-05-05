package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtist_ArtistId(Long artistId);
    List<Album> findByTitleContainingIgnoreCase(String title);
    List<Album> findAllByOrderByReleaseDateDesc(Pageable pageable);
}
