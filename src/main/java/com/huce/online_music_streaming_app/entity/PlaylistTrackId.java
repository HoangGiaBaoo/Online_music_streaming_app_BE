package com.huce.online_music_streaming_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrackId implements Serializable {

    @Column(name = "playlist_id")
    private Long playlistId;

    @Column(name = "track_id")
    private Long trackId;
}