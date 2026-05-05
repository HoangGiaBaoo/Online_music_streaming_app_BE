package com.huce.online_music_streaming_app.dto;

public record TopTrackDto(Long trackId, String title, String coverUrl, String artistName, Long playCount) {}
