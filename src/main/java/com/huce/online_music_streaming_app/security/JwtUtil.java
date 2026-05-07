package com.huce.online_music_streaming_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String generateToken(String username) {
        return buildToken(username, expiration, TYPE_ACCESS);
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, refreshExpiration, TYPE_REFRESH);
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        Claims claims = extractClaims(token);
        return claims.getSubject().equals(username)
                && !claims.getExpiration().before(new Date())
                && TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public boolean isRefreshTokenValid(String token) {
        Claims claims = extractClaims(token);
        return !claims.getExpiration().before(new Date())
                && TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public long getAccessExpirationSeconds() {
        return expiration / 1000;
    }

    private String buildToken(String username, long ttlMillis, String type) {
        return Jwts.builder()
                .subject(username)
                .claims(Map.of(CLAIM_TYPE, type))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttlMillis))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
