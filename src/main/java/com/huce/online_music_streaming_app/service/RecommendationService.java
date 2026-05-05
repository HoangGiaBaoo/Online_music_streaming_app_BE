package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.entity.Artist;
import com.huce.online_music_streaming_app.entity.Track;
import com.huce.online_music_streaming_app.repository.ArtistRepository;
import com.huce.online_music_streaming_app.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;

    public List<Track> relatedTracks(Long trackId, int limit) {
        List<Track> related = trackRepository.findRelatedByGenre(trackId, PageRequest.of(0, limit));
        if (!related.isEmpty()) {
            return related;
        }
        return trackRepository.findById(trackId)
                .map(t -> trackRepository.findByArtist_ArtistIdOrderByPlayCountDesc(
                        t.getArtist().getArtistId(), PageRequest.of(0, limit)))
                .orElse(List.of());
    }

    public List<Artist> relatedArtists(Long artistId, int limit) {
        return artistRepository.findRelatedByGenre(artistId, PageRequest.of(0, limit));
    }

    public List<Track> dailyMix(Long userId, int limit) {
        return trackRepository.findAllByOrderByPlayCountDesc(PageRequest.of(0, limit));
    }
}
