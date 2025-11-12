package com.commerce.e_commerce.dto.security;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String avatarUrl,
        String phone,
        List<String> roles
) {}
