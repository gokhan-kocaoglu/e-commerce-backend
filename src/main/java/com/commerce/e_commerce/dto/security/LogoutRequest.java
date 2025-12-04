package com.commerce.e_commerce.dto.security;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank String refreshToken,
        Boolean logoutAll
) {}
