package com.commerce.e_commerce.dto.catalog;

import java.util.UUID;

public record ProductImageResponse(
        UUID id,
        String url,
        String altText,
        int sortOrder
) {}
