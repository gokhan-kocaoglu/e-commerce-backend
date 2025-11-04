package com.commerce.e_commerce.dto.catalog;

import com.commerce.e_commerce.dto.common.MoneyDto;

import java.util.UUID;

public record ProductListItemResponse(
        UUID id,
        String title,
        String slug,
        UUID categoryId,
        MoneyDto price,
        double ratingAvg,
        int ratingCount,
        double bestsellerScore,
        String thumbnailUrl
) {}
