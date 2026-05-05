package com.huce.online_music_streaming_app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Liked_Tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedTrack {

    @EmbeddedId
    private LikedTrackId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("trackId")
    @JoinColumn(name = "track_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Track track;

    @Column(name = "liked_at")
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();
}