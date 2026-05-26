package com.huce.online_music_streaming_app.dto;

import com.huce.online_music_streaming_app.entity.Playlist;

public record PlaylistResponse(
        Long playlistId,
        String name,
        String coverUrl,
        String coverColor,
        Boolean isPublic,
        Boolean isCurated,
        String mood,
        String createdAt
) {
    public static PlaylistResponse from(Playlist p) {
        return new PlaylistResponse(
                p.getPlaylistId(),
                p.getName(),
                p.getCoverUrl(),
                p.getCoverColor(),
                p.getIsPublic(),
                p.getIsCurated(),
                p.getMood() == null ? null : p.getMood().name(),
                p.getCreatedAt() == null ? null : p.getCreatedAt().toString()
        );
    }
}
