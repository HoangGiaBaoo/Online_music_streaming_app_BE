package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}