package com.commerce.e_commerce.dto.catalog;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record ProductCreateRequest(
        @NotBlank @Size(max=180) String title,
        @Size(max=2000) String description,
        @NotBlank @Size(max=160) String slug,
        @NotNull UUID categoryId,
        UUID brandId,
        @NotNull @Min(0) Long priceCents,
        Long compareAtPriceCents,
        List<String> imageUrls
) {}
