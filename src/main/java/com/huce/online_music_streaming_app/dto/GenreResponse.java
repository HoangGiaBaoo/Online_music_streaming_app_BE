package com.huce.online_music_streaming_app.dto;

import com.huce.online_music_streaming_app.entity.Genre;

public record GenreResponse(Long genreId, String name, String coverColor, String coverUrl) {
    public static GenreResponse from(Genre g) {
        return new GenreResponse(g.getGenreId(), g.getName(), g.getCoverColor(), g.getCoverUrl());
    }
}
