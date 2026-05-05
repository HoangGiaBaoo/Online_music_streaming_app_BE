package com.huce.online_music_streaming_app.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String displayName;
    private String bio;
    private String avatarUrl;
}
