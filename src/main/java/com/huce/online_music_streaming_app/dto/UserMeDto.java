package com.huce.online_music_streaming_app.dto;

import com.huce.online_music_streaming_app.entity.SubscriptionPlan;

public record UserMeDto(
        Long userId,
        String username,
        String displayName,
        String email,
        String avatarUrl,
        String bio,
        String role,
        SubscriptionPlan plan
) {}
