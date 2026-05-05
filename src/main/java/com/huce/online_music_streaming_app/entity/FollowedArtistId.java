package com.huce.online_music_streaming_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowedArtistId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "artist_id")
    private Long artistId;
}