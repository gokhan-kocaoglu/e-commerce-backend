package com.commerce.e_commerce.dto.catalog;

import java.time.Instant;
import java.util.UUID;

public record RatingResponse(
        UUID productId,
        UUID userId,
        int rating,
        String comment,
        Instant createdAt
) {}
