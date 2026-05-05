package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.dto.HomeSectionDto;
import com.huce.online_music_streaming_app.entity.*;
import com.huce.online_music_streaming_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeFeedService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final PlayHistoryRepository playHistoryRepository;
    private final FollowedArtistRepository followedArtistRepository;
    private final RecommendationService recommendationService;

    public List<HomeSectionDto> buildFeed(Long userId, String filter) {
        List<HomeSectionDto> sections = new ArrayList<>();
        boolean musicOnly = "music".equalsIgnoreCase(filter);
        boolean followingOnly = "following".equalsIgnoreCase(filter);

        if (followingOnly) {
            sections.add(followingSection(userId));
            return sections;
        }

        List<Playlist> curated = playlistRepository.findByIsCuratedTrue();
        if (!curated.isEmpty()) {
            sections.add(HomeSectionDto.builder()
                    .kind("FEATURED")
                    .title(null)
                    .items(List.of(curated.get(0)))
                    .build());
            sections.add(HomeSectionDto.builder()
                    .kind("TOP_PICKS")
                    .title("Tuyển tập hàng đầu của bạn")
                    .items(curated.stream().limit(8).toList())
                    .build());
        }

        List<Track> recent = playHistoryRepository
                .findByUser_UserIdOrderByPlayedAtDesc(userId, PageRequest.of(0, 10))
                .stream().map(PlayHistory::getTrack).distinct().toList();
        if (!recent.isEmpty()) {
            sections.add(HomeSectionDto.builder()
                    .kind("RECENTLY_PLAYED")
                    .title("Gần đây")
                    .subtitle("Hiện tất cả")
                    .items(recent)
                    .build());
        }

        sections.add(HomeSectionDto.builder()
                .kind("RECOMMENDED")
                .title("Được đề xuất cho hôm nay")
                .items(recommendationService.dailyMix(userId, 10))
                .build());

        sections.add(HomeSectionDto.builder()
                .kind("CHART")
                .title("Bảng xếp hạng")
                .items(trackRepository.findAllByOrderByPlayCountDesc(PageRequest.of(0, 10)))
                .build());

        addMoodSection(sections, Mood.NOSTALGIC, "Hoài niệm");
        addMoodSection(sections, Mood.WORKOUT, "Tập luyện");
        addMoodSection(sections, Mood.PARTY, "Tiệc tùng");
        addMoodSection(sections, Mood.HAPPY, "Vui vẻ");
        addMoodSection(sections, Mood.RELAX, "Thư giãn");
        addMoodSection(sections, Mood.KARAOKE, "Hát theo");

        sections.add(HomeSectionDto.builder()
                .kind("NEW_RELEASES")
                .title("Album và đĩa đơn nổi tiếng")
                .items(albumRepository.findAllByOrderByReleaseDateDesc(PageRequest.of(0, 10)))
                .build());

        sections.add(HomeSectionDto.builder()
                .kind("POPULAR_ARTISTS")
                .title("Nghệ sĩ phổ biến")
                .items(popularArtists(10))
                .build());

        if (musicOnly) {
            sections.removeIf(s -> "FEATURED".equals(s.getKind()));
        }
        return sections;
    }

    private void addMoodSection(List<HomeSectionDto> sections, Mood mood, String title) {
        List<Playlist> items = playlistRepository.findByIsCuratedTrueAndMood(mood);
        if (items.isEmpty()) return;
        sections.add(HomeSectionDto.builder()
                .kind("MOOD_PLAYLIST")
                .title(title)
                .items(items)
                .build());
    }

    private HomeSectionDto followingSection(Long userId) {
        List<Artist> followed = followedArtistRepository.findByIdUserId(userId)
                .stream().map(FollowedArtist::getArtist).toList();
        return HomeSectionDto.builder()
                .kind("POPULAR_ARTISTS")
                .title("Đang theo dõi")
                .items(followed)
                .build();
    }

    private List<Artist> popularArtists(int limit) {
        return artistRepository.findPopularWithFollowers(PageRequest.of(0, limit))
                .stream().map(row -> (Artist) row[0]).toList();
    }
}
