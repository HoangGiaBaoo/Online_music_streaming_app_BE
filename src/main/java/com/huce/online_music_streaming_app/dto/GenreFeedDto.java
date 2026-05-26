package com.huce.online_music_streaming_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class GenreFeedDto {
    private GenreResponse genre;
    private List<GenreSectionDto> sections;
}
