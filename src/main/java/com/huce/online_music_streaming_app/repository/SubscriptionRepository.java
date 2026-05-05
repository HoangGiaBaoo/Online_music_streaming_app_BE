package com.huce.online_music_streaming_app.repository;

import com.huce.online_music_streaming_app.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findFirstByUser_UserIdAndActiveTrueOrderByStartDateDesc(Long userId);
}
