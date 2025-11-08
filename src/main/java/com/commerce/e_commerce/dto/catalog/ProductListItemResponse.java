package com.commerce.e_commerce.dto.catalog;

import com.commerce.e_commerce.dto.common.MoneyDto;

import java.util.UUID;

public record ProductListItemResponse(
        UUID id,
        String title,
        String description,
        String slug,
        UUID categoryId,
        MoneyDto price,
        MoneyDto compareAtPrice,
        double ratingAvg,
        int ratingCount,
        double bestsellerScore,
        String thumbnailUrl
) {}
