package com.huce.online_music_streaming_app.dto;

import com.huce.online_music_streaming_app.entity.SubscriptionPlan;
import lombok.Data;

@Data
public class SubscribeRequest {
    private SubscriptionPlan plan;
}
