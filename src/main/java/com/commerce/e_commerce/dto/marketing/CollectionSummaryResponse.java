package com.commerce.e_commerce.dto.marketing;

import java.util.UUID;

public record CollectionSummaryResponse(
        UUID id,
        String name,
        String slug,
        String shortDescription,
        String ctaText,
        String heroImageUrl,
        long itemCount
) {}
