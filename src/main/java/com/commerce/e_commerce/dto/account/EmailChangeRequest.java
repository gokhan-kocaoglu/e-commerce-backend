package com.commerce.e_commerce.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailChangeRequest(
        @Email @NotBlank String newEmail,
        @NotBlank @Size(min=8, max=128) String currentPassword
) {}
