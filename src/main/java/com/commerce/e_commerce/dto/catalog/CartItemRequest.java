package com.commerce.e_commerce.dto.catalog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CartItemRequest(
        @NotNull UUID variantId,
        @Positive int quantity
) {}
