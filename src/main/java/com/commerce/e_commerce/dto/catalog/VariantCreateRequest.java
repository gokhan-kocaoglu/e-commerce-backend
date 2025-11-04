package com.commerce.e_commerce.dto.catalog;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record VariantCreateRequest(
        @NotNull UUID productId,
        @NotBlank @Size(max=80) String sku,
        @NotNull @Min(0) Long priceCents,
        Long compareAtPriceCents,
        String attributesJson
) {}
