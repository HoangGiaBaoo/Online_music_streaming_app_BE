package com.huce.online_music_streaming_app.dto;

import java.time.LocalDate;

public record ListeningStatsDto(
        String periodLabel,
        LocalDate periodStart,
        LocalDate periodEnd,
        TopArtistDto topArtist,
        TopTrackDto topTrack,
        Long totalPlays,
        Long totalMinutes
) {}
