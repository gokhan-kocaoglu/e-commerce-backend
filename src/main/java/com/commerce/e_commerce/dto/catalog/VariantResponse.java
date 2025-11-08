package com.commerce.e_commerce.dto.catalog;

import com.commerce.e_commerce.dto.common.MoneyDto;

import java.util.Map;
import java.util.UUID;

public record VariantResponse(
        UUID id,
        UUID productId,
        String sku,
        MoneyDto priceCents,
        MoneyDto compareAtPriceCents,
        Map<String, String> attributes
) {}
