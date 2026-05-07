package com.huce.online_music_streaming_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private long expiresIn;
    private String username;
    private String role;
}
