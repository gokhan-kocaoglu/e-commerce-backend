package com.commerce.e_commerce.dto.catalog;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Map;

public record VariantUpdateRequest(
        String sku,
        BigDecimal priceCents,
        BigDecimal compareAtPriceCents,
        @Schema(
                type = "object",
                additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                example = "{\"size\":\"M\",\"color\":\"Black\"}"
        )
        Map<String, String> attributes
) {}
