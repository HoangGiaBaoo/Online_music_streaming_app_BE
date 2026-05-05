package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.dto.UserSettingsDto;
import com.huce.online_music_streaming_app.entity.User;
import com.huce.online_music_streaming_app.entity.UserSettings;
import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserSettings getOrCreate(Long userId) {
        return userSettingsRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    UserSettings settings = UserSettings.builder().user(user).build();
                    return userSettingsRepository.save(settings);
                });
    }

    @Transactional
    public UserSettings update(Long userId, UserSettingsDto dto) {
        UserSettings settings = getOrCreate(userId);
        if (dto.getDataSaver() != null) settings.setDataSaver(dto.getDataSaver());
        if (dto.getPushNotifications() != null) settings.setPushNotifications(dto.getPushNotifications());
        if (dto.getStreamQualityWifi() != null) settings.setStreamQualityWifi(dto.getStreamQualityWifi());
        if (dto.getPrivateSession() != null) settings.setPrivateSession(dto.getPrivateSession());
        if (dto.getPersonalizedAds() != null) settings.setPersonalizedAds(dto.getPersonalizedAds());
        return userSettingsRepository.save(settings);
    }
}
