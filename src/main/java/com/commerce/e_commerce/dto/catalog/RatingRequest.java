package com.commerce.e_commerce.dto.catalog;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RatingRequest(
        @NotNull UUID productId,
        @Min(1) @Max(5) int rating,
        @Size(max=800) String comment
) {}
