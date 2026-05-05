package com.huce.online_music_streaming_app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "Albums")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Long albumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "album_type", length = 20)
    @Builder.Default
    private AlbumType albumType = AlbumType.ALBUM;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}