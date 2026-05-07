package com.huce.online_music_streaming_app.config;

import com.huce.online_music_streaming_app.entity.*;
import com.huce.online_music_streaming_app.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final String PLACEHOLDER_AUDIO = "/audio/placeholder.mp3";
    private static final String PLACEHOLDER_COVER = "/images/placeholder.jpg";

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (artistRepository.count() > 0 || trackRepository.count() > 0) {
            log.info("DataSeeder: data already present, skipping seed.");
            return;
        }
        log.info("DataSeeder: empty DB detected, seeding sample data...");

        User editor = seedEditorUser();
        List<Genre> genres = seedGenres();
        List<Artist> artists = seedArtists();
        List<Album> albums = seedAlbums(artists);
        List<Track> tracks = seedTracks(artists, albums, genres);
        seedCuratedPlaylists(editor, tracks);

        log.info("DataSeeder: done.");
    }

    private User seedEditorUser() {
        return userRepository.findByUsername("spotify_editor").orElseGet(() ->
                userRepository.save(User.builder()
                        .username("spotify_editor")
                        .email("editor@musicapp.local")
                        .passwordHash(passwordEncoder.encode("editor-internal"))
                        .displayName("Spotify Editor")
                        .role("admin")
                        .build()));
    }

    private List<Genre> seedGenres() {
        String[][] data = {
                {"Pop", "#E91E63"},
                {"Rock", "#9C27B0"},
                {"Hip-Hop", "#FF9800"},
                {"R&B", "#673AB7"},
                {"Electronic", "#03A9F4"},
                {"Jazz", "#795548"},
                {"Classical", "#607D8B"},
                {"Country", "#8BC34A"},
                {"Indie", "#00BCD4"},
                {"V-Pop", "#F44336"},
                {"Bolero", "#3F51B5"},
                {"EDM", "#4CAF50"}
        };
        List<Genre> list = new ArrayList<>();
        for (String[] row : data) {
            list.add(genreRepository.save(Genre.builder()
                    .name(row[0])
                    .coverColor(row[1])
                    .coverUrl(PLACEHOLDER_COVER)
                    .build()));
        }
        return list;
    }

    private List<Artist> seedArtists() {
        String[][] data = {
                {"Sơn Tùng M-TP", "Ca sĩ Việt Nam, sinh năm 1994"},
                {"Mỹ Tâm", "Diva nhạc Việt"},
                {"Đen Vâu", "Rapper Việt Nam"},
                {"Hoàng Thùy Linh", "Ca sĩ kết hợp dân gian đương đại"},
                {"Vũ.", "Singer-songwriter"},
                {"Bích Phương", "Ca sĩ pop ballad"},
                {"Hà Anh Tuấn", "Ca sĩ pop"},
                {"Mr. Siro", "Singer-songwriter"},
                {"Min", "Ca sĩ V-Pop"},
                {"Karik", "Rapper"}
        };
        List<Artist> list = new ArrayList<>();
        for (String[] row : data) {
            list.add(artistRepository.save(Artist.builder()
                    .name(row[0])
                    .bio(row[1])
                    .avatarUrl(PLACEHOLDER_COVER)
                    .build()));
        }
        return list;
    }

    private List<Album> seedAlbums(List<Artist> artists) {
        Object[][] data = {
                {0, "M-TP M-TP", AlbumType.ALBUM, LocalDate.now().minusMonths(6)},
                {0, "Hãy Trao Cho Anh", AlbumType.SINGLE, LocalDate.now().minusMonths(2)},
                {1, "Tâm 9", AlbumType.ALBUM, LocalDate.now().minusYears(1)},
                {1, "Đúng Cũng Thành Sai", AlbumType.SINGLE, LocalDate.now().minusMonths(3)},
                {2, "Show Của Đen", AlbumType.ALBUM, LocalDate.now().minusMonths(8)},
                {2, "Đi Về Nhà", AlbumType.SINGLE, LocalDate.now().minusMonths(1)},
                {3, "Hoàng", AlbumType.ALBUM, LocalDate.now().minusYears(2)},
                {3, "See Tình", AlbumType.SINGLE, LocalDate.now().minusMonths(5)},
                {4, "Bảo Tàng Của Nuối Tiếc", AlbumType.EP, LocalDate.now().minusMonths(4)},
                {5, "Bùa Yêu", AlbumType.SINGLE, LocalDate.now().minusMonths(7)},
                {6, "Truyện Ngắn", AlbumType.ALBUM, LocalDate.now().minusMonths(9)},
                {7, "Bao Giờ Lấy Chồng", AlbumType.SINGLE, LocalDate.now().minusYears(1)},
                {8, "On & On", AlbumType.EP, LocalDate.now().minusMonths(10)},
                {9, "Người Lạ Ơi", AlbumType.SINGLE, LocalDate.now().minusMonths(3)},
                {0, "Chúng Ta Của Hiện Tại", AlbumType.SINGLE, LocalDate.now().minusMonths(11)}
        };
        List<Album> list = new ArrayList<>();
        for (Object[] row : data) {
            list.add(albumRepository.save(Album.builder()
                    .artist(artists.get((int) row[0]))
                    .title((String) row[1])
                    .albumType((AlbumType) row[2])
                    .releaseDate((LocalDate) row[3])
                    .coverUrl(PLACEHOLDER_COVER)
                    .description("Album mẫu cho mục đích kiểm thử.")
                    .build()));
        }
        return list;
    }

    private List<Track> seedTracks(List<Artist> artists, List<Album> albums, List<Genre> genres) {
        Object[][] data = {
                {0, 0, "Lạc Trôi", 240, 9},
                {0, 0, "Nơi Này Có Anh", 230, 9},
                {0, 1, "Hãy Trao Cho Anh", 250, 9},
                {0, 14, "Chúng Ta Của Hiện Tại", 280, 9},
                {1, 2, "Như Một Giấc Mơ", 260, 0},
                {1, 2, "Đâu Chỉ Riêng Em", 245, 0},
                {1, 3, "Đúng Cũng Thành Sai", 230, 0},
                {2, 4, "Lối Nhỏ", 220, 2},
                {2, 4, "Mang Tiền Về Cho Mẹ", 270, 2},
                {2, 5, "Đi Về Nhà", 230, 2},
                {3, 6, "Để Mị Nói Cho Mà Nghe", 235, 9},
                {3, 6, "Bánh Trôi Nước", 250, 9},
                {3, 7, "See Tình", 215, 11},
                {4, 8, "Lời Tạm Biệt Chưa Nói", 280, 8},
                {4, 8, "Bước Qua Nhau", 265, 8},
                {5, 9, "Bùa Yêu", 240, 0},
                {5, 9, "Đi Đu Đưa Đi", 215, 0},
                {6, 10, "Tháng Tư Là Lời Nói Dối Của Em", 280, 0},
                {6, 10, "Người Con Gái Ta Thương", 295, 0},
                {7, 11, "Bao Giờ Lấy Chồng", 245, 0},
                {7, 11, "Một Bước Yêu Vạn Dặm Đau", 260, 0},
                {8, 12, "Có Em Chờ", 220, 11},
                {8, 12, "Vì Yêu Cứ Đâm Đầu", 235, 11},
                {9, 13, "Người Lạ Ơi", 230, 2},
                {9, 13, "Anh Đếch Cần Gì Nhiều Ngoài Em", 245, 2},
                {0, 0, "Em Của Ngày Hôm Qua", 235, 0},
                {1, 2, "Hẹn Ước Từ Hư Vô", 270, 0},
                {2, 4, "Anh Đếch Cần Gì Nhiều Ngoài Em (Remix)", 250, 2},
                {3, 6, "Kẻ Cắp Gặp Bà Già", 240, 9},
                {4, 8, "Đôi Lời Tâm Sự", 275, 8}
        };
        List<Track> list = new ArrayList<>();
        for (Object[] row : data) {
            int artistIdx = (int) row[0];
            int albumIdx = (int) row[1];
            String title = (String) row[2];
            int duration = (int) row[3];
            int genreIdx = (int) row[4];

            Track track = Track.builder()
                    .artist(artists.get(artistIdx))
                    .album(albums.get(albumIdx))
                    .title(title)
                    .duration(duration)
                    .audioUrl(PLACEHOLDER_AUDIO)
                    .coverUrl(PLACEHOLDER_COVER)
                    .playCount((int) (Math.random() * 10000))
                    .genres(Set.of(genres.get(genreIdx)))
                    .build();
            list.add(trackRepository.save(track));
        }
        return list;
    }

    private void seedCuratedPlaylists(User editor, List<Track> tracks) {
        Object[][] data = {
                {"Tập luyện", Mood.WORKOUT, "#FF5722", "Năng lượng cao cho buổi tập."},
                {"Hoài niệm", Mood.NOSTALGIC, "#9C27B0", "Những bản hit gợi nhớ thanh xuân."},
                {"Tiệc tùng", Mood.PARTY, "#E91E63", "Quẩy hết mình cùng bạn bè."},
                {"Vui vẻ", Mood.HAPPY, "#FFC107", "Bài hát giúp ngày của bạn tươi sáng."},
                {"Thư giãn", Mood.RELAX, "#4CAF50", "Chậm lại và hít thở."}
        };
        for (int i = 0; i < data.length; i++) {
            Object[] row = data[i];
            Playlist playlist = playlistRepository.save(Playlist.builder()
                    .user(editor)
                    .name((String) row[0])
                    .mood((Mood) row[1])
                    .coverColor((String) row[2])
                    .description((String) row[3])
                    .coverUrl(PLACEHOLDER_COVER)
                    .isCurated(true)
                    .isPublic(true)
                    .build());

            int start = (i * 5) % tracks.size();
            for (int pos = 0; pos < 5; pos++) {
                Track track = tracks.get((start + pos) % tracks.size());
                playlistTrackRepository.save(PlaylistTrack.builder()
                        .id(new PlaylistTrackId(playlist.getPlaylistId(), track.getTrackId()))
                        .playlist(playlist)
                        .track(track)
                        .position(pos + 1)
                        .build());
            }
        }
    }
}
