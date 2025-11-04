package com.commerce.e_commerce.dto.catalog;

import java.util.UUID;

public record BrandResponse(
        UUID id,
        String name,
        String slug,
        String logoUrl
) {}
