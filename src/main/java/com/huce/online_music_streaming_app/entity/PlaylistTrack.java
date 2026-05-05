package com.huce.online_music_streaming_app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Playlist_Tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistTrack {

    @EmbeddedId
    private PlaylistTrackId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("trackId")
    @JoinColumn(name = "track_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Track track;

    @Column(name = "position", nullable = false)
    private Integer position;
}