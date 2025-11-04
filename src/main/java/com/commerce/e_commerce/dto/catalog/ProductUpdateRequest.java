package com.commerce.e_commerce.dto.catalog;

import java.util.List;
import java.util.UUID;

public record ProductUpdateRequest(
        String title,
        String description,
        String slug,
        UUID categoryId,
        UUID brandId,
        Long priceCents,
        Long compareAtPriceCents,
        List<String> imageUrls
) {}
