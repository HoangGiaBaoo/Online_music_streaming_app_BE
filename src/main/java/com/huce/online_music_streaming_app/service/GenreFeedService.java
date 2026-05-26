package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.dto.GenreFeedDto;
import com.huce.online_music_streaming_app.dto.GenreResponse;
import com.huce.online_music_streaming_app.dto.GenreSectionDto;
import com.huce.online_music_streaming_app.entity.Artist;
import com.huce.online_music_streaming_app.entity.Genre;
import com.huce.online_music_streaming_app.entity.Playlist;
import com.huce.online_music_streaming_app.entity.Track;
import com.huce.online_music_streaming_app.repository.AlbumRepository;
import com.huce.online_music_streaming_app.repository.ArtistRepository;
import com.huce.online_music_streaming_app.repository.GenreRepository;
import com.huce.online_music_streaming_app.repository.PlaylistRepository;
import com.huce.online_music_streaming_app.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreFeedService {

    private static final int SECTION_LIMIT = 10;
    private static final int RELATED_GENRES_LIMIT = 8;

    private final GenreRepository genreRepository;
    private final PlaylistRepository playlistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;

    public GenreFeedDto buildFeed(Long genreId, Long userId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found: " + genreId));
        String name = genre.getName();

        List<GenreSectionDto> sections = new ArrayList<>();

        List<Playlist> popularPlaylists = playlistRepository.findByIsCuratedTrue()
                .stream().limit(SECTION_LIMIT).toList();
        addSection(sections, "POPULAR_PLAYLISTS",
                "Danh sách phát " + name + " phổ biến", popularPlaylists);

        addSection(sections, "NEW_RELEASES",
                "Những bản phát hành " + name + " mới",
                albumRepository.findByGenreId(genreId, PageRequest.of(0, SECTION_LIMIT)));

        List<Track> popularTracks = trackRepository.findByGenreId(genreId).stream()
                .sorted(Comparator.comparing(Track::getPlayCount,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(SECTION_LIMIT)
                .toList();
        addSection(sections, "POPULAR_TRACKS",
                "Bài hát " + name + " nổi bật", popularTracks);

        List<Artist> popularArtists = artistRepository
                .findByGenreIdOrderByFollowers(genreId, PageRequest.of(0, SECTION_LIMIT))
                .stream().map(row -> (Artist) row[0]).toList();
        addSection(sections, "POPULAR_ARTISTS",
                "Nghệ sĩ " + name + " nổi bật", popularArtists);

        List<Genre> relatedGenres = genreRepository.findAll().stream()
                .filter(g -> !g.getGenreId().equals(genreId))
                .limit(RELATED_GENRES_LIMIT)
                .toList();
        addSection(sections, "RELATED_GENRES", "Thể loại liên quan", relatedGenres);

        return GenreFeedDto.builder()
                .genre(GenreResponse.from(genre))
                .sections(sections)
                .build();
    }

    private void addSection(List<GenreSectionDto> sections, String kind, String title, List<?> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        sections.add(GenreSectionDto.builder()
                .kind(kind)
                .title(title)
                .items(items)
                .build());
    }
}
