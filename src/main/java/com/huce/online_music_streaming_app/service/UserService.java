package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.dto.ProfileUpdateRequest;
import com.huce.online_music_streaming_app.dto.UserMeDto;
import com.huce.online_music_streaming_app.dto.UserProfileDto;
import com.huce.online_music_streaming_app.entity.Subscription;
import com.huce.online_music_streaming_app.entity.User;
import com.huce.online_music_streaming_app.repository.FollowedArtistRepository;
import com.huce.online_music_streaming_app.repository.PlaylistRepository;
import com.huce.online_music_streaming_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final PlaylistRepository playlistRepository;
    private final FollowedArtistRepository followedArtistRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public UserMeDto getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        Subscription sub = subscriptionService.getCurrent(userId);
        return new UserMeDto(
                user.getUserId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getRole(),
                sub.getPlan()
        );
    }

    public UserProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        long following = followedArtistRepository.findByIdUserId(userId).size();
        return new UserProfileDto(
                user.getUserId(),
                user.getDisplayName() != null ? user.getDisplayName() : user.getUsername(),
                user.getAvatarUrl(),
                user.getBio(),
                0L,
                following,
                playlistRepository.findByUser_UserId(userId)
        );
    }

    public UserMeDto updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);
        return getMe(userId);
    }
}
