package com.huce.online_music_streaming_app.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
