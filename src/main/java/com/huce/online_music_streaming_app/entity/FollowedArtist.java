package com.huce.online_music_streaming_app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Followed_Artists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowedArtist {

    @EmbeddedId
    private FollowedArtistId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Artist artist;

    @Column(name = "followed_at")
    @Builder.Default
    private LocalDateTime followedAt = LocalDateTime.now();
}