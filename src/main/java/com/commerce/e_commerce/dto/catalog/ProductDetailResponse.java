package com.commerce.e_commerce.dto.catalog;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ProductDetailResponse(
        UUID id,
        UUID productId,
        String shortSummary,
        List<ProductDetailRequest.Section> sections,
        Map<String, String> additionalInfo
) {}
