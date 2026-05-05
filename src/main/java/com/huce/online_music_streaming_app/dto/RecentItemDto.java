package com.huce.online_music_streaming_app.dto;

import com.huce.online_music_streaming_app.entity.Track;

import java.time.LocalDateTime;

public record RecentItemDto(Track track, LocalDateTime playedAt) {
}