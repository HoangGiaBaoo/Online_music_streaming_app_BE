package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.entity.*;
import com.huce.online_music_streaming_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayHistoryService {

    private final PlayHistoryRepository playHistoryRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    public List<PlayHistory> getHistory(Long userId) {
        return playHistoryRepository.findByUser_UserIdOrderByPlayedAtDesc(userId);
    }

    public void record(Long userId, Long trackId) {
        User user = userRepository.findById(userId).orElseThrow();
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found: " + trackId));
        PlayHistory history = PlayHistory.builder()
                .user(user)
                .track(track)
                .build();
        playHistoryRepository.save(history);
    }
}
