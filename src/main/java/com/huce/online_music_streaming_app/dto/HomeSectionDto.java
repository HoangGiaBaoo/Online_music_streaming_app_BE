package com.huce.online_music_streaming_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class HomeSectionDto {
    private String kind;
    private String title;
    private String subtitle;
    private List<?> items;
}
