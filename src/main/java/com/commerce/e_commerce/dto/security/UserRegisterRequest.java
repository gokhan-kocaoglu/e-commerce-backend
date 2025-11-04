package com.commerce.e_commerce.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min=8, max=128) String password,
        @NotBlank String firstName,
        @NotBlank String lastName
) {}
