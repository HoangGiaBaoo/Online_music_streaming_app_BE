package com.huce.online_music_streaming_app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long genreId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "cover_color", length = 10)
    private String coverColor;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;
}