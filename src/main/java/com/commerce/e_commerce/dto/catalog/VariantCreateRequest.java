package com.commerce.e_commerce.dto.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record VariantCreateRequest(
        @NotNull UUID productId,
        @NotBlank @Size(max=80) String sku,
        @NotNull @Min(0) BigDecimal priceCents,
        BigDecimal compareAtPriceCents,
        @Schema(
                type = "object",
                additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                example = "{\"size\":\"M\",\"color\":\"Black\"}"
        )
        Map<String, String> attributes
) {}
