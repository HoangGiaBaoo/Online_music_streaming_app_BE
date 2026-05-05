package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.entity.*;
import com.huce.online_music_streaming_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LikedTrackRepository likedTrackRepository;
    private final FollowedArtistRepository followedArtistRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;

    public List<LikedTrack> getLikedTracks(Long userId) {
        return likedTrackRepository.findByIdUserId(userId);
    }

    @Transactional
    public void toggleLike(Long userId, Long trackId) {
        if (likedTrackRepository.existsByIdUserIdAndIdTrackId(userId, trackId)) {
            likedTrackRepository.deleteByIdUserIdAndIdTrackId(userId, trackId);
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            Track track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new RuntimeException("Track not found: " + trackId));
            LikedTrack liked = LikedTrack.builder()
                    .id(new LikedTrackId(userId, trackId))
                    .user(user)
                    .track(track)
                    .build();
            likedTrackRepository.save(liked);
        }
    }

    public List<FollowedArtist> getFollowedArtists(Long userId) {
        return followedArtistRepository.findByIdUserId(userId);
    }

    @Transactional
    public void toggleFollow(Long userId, Long artistId) {
        if (followedArtistRepository.existsByIdUserIdAndIdArtistId(userId, artistId)) {
            followedArtistRepository.deleteByIdUserIdAndIdArtistId(userId, artistId);
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Artist not found: " + artistId));
            FollowedArtist followed = FollowedArtist.builder()
                    .id(new FollowedArtistId(userId, artistId))
                    .user(user)
                    .artist(artist)
                    .build();
            followedArtistRepository.save(followed);
        }
    }
}
