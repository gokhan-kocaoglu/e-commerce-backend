package com.commerce.e_commerce.dto.catalog;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductCreateRequest(
        @NotBlank String title,
        String description,
        @NotBlank String slug,
        @NotNull UUID categoryId,
        @NotNull UUID brandId,

        @JsonAlias({"priceCents"})
        @NotNull @DecimalMin("0.00") BigDecimal price,

        @JsonAlias({"compareAtPriceCents"})
        @DecimalMin("0.00") BigDecimal compareAtPrice,

        List<String> imageUrls
) {}
