package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.dto.*;
import com.huce.online_music_streaming_app.entity.User;
import com.huce.online_music_streaming_app.repository.UserRepository;
import com.huce.online_music_streaming_app.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserSettingsService userSettingsService;

    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getUsername())
                .role("user")
                .build();

        User saved = userRepository.save(user);
        userSettingsService.getOrCreate(saved.getUserId());
        return saved;
    }

    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return buildResponse(user);
    }

    public JwtResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank() || !jwtUtil.isRefreshTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return buildResponse(user);
    }

    private JwtResponse buildResponse(User user) {
        String accessToken = jwtUtil.generateToken(user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return new JwtResponse(
                accessToken,
                newRefreshToken,
                jwtUtil.getAccessExpirationSeconds(),
                user.getUsername(),
                user.getRole()
        );
    }
}
