package com.huce.online_music_streaming_app.dto;

import lombok.Data;

@Data
public class PlaylistRequest {
    private String name;
    private Boolean isPublic = false;
}
