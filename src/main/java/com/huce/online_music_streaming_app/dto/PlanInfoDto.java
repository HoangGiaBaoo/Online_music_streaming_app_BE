package com.huce.online_music_streaming_app.dto;

import com.huce.online_music_streaming_app.entity.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PlanInfoDto {
    private SubscriptionPlan plan;
    private String name;
    private Long priceVnd;
    private List<String> features;
}
