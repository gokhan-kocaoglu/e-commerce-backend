package com.commerce.e_commerce.dto.catalog;

public record ProductImageUpdateRequest(
        String url,
        String altText,
        Integer sortOrder
) {}
