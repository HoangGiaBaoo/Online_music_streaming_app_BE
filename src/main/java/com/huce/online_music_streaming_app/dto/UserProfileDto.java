package com.huce.online_music_streaming_app.dto;

import com.huce.online_music_streaming_app.entity.Playlist;

import java.util.List;

public record UserProfileDto(
        Long userId,
        String displayName,
        String avatarUrl,
        String bio,
        long followers,
        long following,
        List<Playlist> playlists
) {}
