package com.huce.online_music_streaming_app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "User_Settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settings_id")
    private Long settingsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "data_saver")
    @Builder.Default
    private Boolean dataSaver = false;

    @Column(name = "push_notifications")
    @Builder.Default
    private Boolean pushNotifications = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "stream_quality_wifi", length = 20)
    @Builder.Default
    private StreamQuality streamQualityWifi = StreamQuality.HIGH;

    @Column(name = "private_session")
    @Builder.Default
    private Boolean privateSession = false;

    @Column(name = "personalized_ads")
    @Builder.Default
    private Boolean personalizedAds = true;
}
