package com.commerce.e_commerce.dto.catalog;

import jakarta.validation.constraints.NotBlank;

public record BrandRequest(
        @NotBlank String name,
        @NotBlank String slug,
        String logoUrl
) {}
