package com.commerce.e_commerce.dto.marketing;

import java.util.UUID;

public record CollectionItemResponse(
        UUID id,
        UUID productId,
        int sortOrder,
        String imageUrl
) {}
