package com.commerce.e_commerce.dto.catalog;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductUpdateRequest(
        String title,
        String description,
        String slug,
        UUID categoryId,
        UUID brandId,
        @JsonAlias({"priceCents"})
        @NotNull @DecimalMin("0.00") BigDecimal price,

        @JsonAlias({"compareAtPriceCents"})
        @DecimalMin("0.00") BigDecimal compareAtPrice,
        List<String> imageUrls
) {}
