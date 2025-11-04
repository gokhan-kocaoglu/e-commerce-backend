package com.commerce.e_commerce.dto.security;

import java.time.Instant;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Instant expiresAt,
        UserResponse user
) {}
