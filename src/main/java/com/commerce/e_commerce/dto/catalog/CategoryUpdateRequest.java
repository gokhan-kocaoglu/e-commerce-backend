package com.commerce.e_commerce.dto.catalog;

import java.util.UUID;

public record CategoryUpdateRequest(
        String name,
        String slug,
        String heroImageUrl,
        UUID parentId
) {}