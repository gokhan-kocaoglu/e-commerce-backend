package com.commerce.e_commerce.dto.order;

import jakarta.validation.constraints.NotBlank;

public record OrderCancelRequest(
        @NotBlank String reason,
        String note
) {}
