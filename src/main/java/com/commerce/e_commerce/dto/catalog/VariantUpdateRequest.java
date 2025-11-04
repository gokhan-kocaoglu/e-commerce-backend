package com.commerce.e_commerce.dto.catalog;

public record VariantUpdateRequest(
        String sku,
        Long priceCents,
        Long compareAtPriceCents,
        String attributesJson
) {}
