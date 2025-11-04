package com.commerce.e_commerce.security;

import com.commerce.e_commerce.domain.security.User;

import java.time.Duration;
import java.util.UUID;

public interface JwtTokenProvider {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean validate(String token);
    UUID getUserId(String token);
    Duration accessTokenTtl();
    Duration refreshTokenTtl();
}
