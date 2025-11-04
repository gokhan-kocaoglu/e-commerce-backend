package com.commerce.e_commerce.dto.catalog;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        String heroImageUrl,
        UUID parentId
) {}
