package com.commerce.e_commerce.dto.catalog;

import com.commerce.e_commerce.dto.common.MoneyDto;

import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String title,
        String description,
        String slug,
        UUID categoryId,
        UUID brandId,
        MoneyDto price,
        MoneyDto compareAtPrice,
        List<Image> images,
        double ratingAvg,
        int ratingCount,
        double bestsellerScore
) {
    public record Image(UUID id, String url, String altText, int sortOrder) {}
}
