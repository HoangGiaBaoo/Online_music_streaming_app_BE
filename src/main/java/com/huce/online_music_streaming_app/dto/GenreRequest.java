package com.huce.online_music_streaming_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GenreRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Pattern(
                regexp = "^#[0-9A-Fa-f]{6}$",
                message = "coverColor phải là hex 6 ký tự, vd #1DB954"
        )
        String coverColor,

        String coverUrl
) {}
