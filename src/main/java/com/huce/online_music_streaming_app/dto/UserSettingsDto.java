package com.huce.online_music_streaming_app.dto;

import com.huce.online_music_streaming_app.entity.StreamQuality;
import lombok.Data;

@Data
public class UserSettingsDto {
    private Boolean dataSaver;
    private Boolean pushNotifications;
    private StreamQuality streamQualityWifi;
    private Boolean privateSession;
    private Boolean personalizedAds;
}
