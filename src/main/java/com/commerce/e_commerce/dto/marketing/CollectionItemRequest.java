package com.commerce.e_commerce.dto.marketing;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CollectionItemRequest(
        @NotNull UUID productId,
        int sortOrder,
        String imageUrl
) {}
