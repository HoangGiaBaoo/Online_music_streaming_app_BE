package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUser_UserId(Long userId);
}
