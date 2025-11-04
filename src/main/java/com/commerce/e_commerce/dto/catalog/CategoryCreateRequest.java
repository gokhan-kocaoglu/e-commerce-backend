package com.commerce.e_commerce.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CategoryCreateRequest(
        @NotBlank @Size(max=120) String name,
        @NotBlank @Size(max=140) String slug,
        String heroImageUrl,
        UUID parentId
) {}
