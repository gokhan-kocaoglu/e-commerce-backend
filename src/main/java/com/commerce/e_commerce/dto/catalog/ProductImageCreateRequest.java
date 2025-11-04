package com.commerce.e_commerce.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductImageCreateRequest(
        @NotNull UUID productId,
        @NotBlank String url,
        String altText,
        Integer sortOrder
) {}
