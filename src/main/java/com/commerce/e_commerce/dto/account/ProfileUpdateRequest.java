package com.commerce.e_commerce.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank @Size(max=80) String firstName,
        @NotBlank @Size(max=80) String lastName,
        @Size(max=30)          String phone,
        @Size(max=300)         String avatarUrl
) {}
