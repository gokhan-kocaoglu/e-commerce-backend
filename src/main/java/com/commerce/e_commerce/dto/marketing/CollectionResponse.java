package com.commerce.e_commerce.dto.marketing;

import java.util.List;
import java.util.UUID;

public record CollectionResponse(
        UUID id,
        String name,
        String slug,
        String shortDescription,
        String ctaText,
        String heroImageUrl,
        List<CollectionItem> items
) {
    public record CollectionItem(
            UUID productId,
            int sortOrder,
            String imageUrl
    ) {}
}
