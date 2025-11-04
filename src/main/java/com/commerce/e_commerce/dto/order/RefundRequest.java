package com.commerce.e_commerce.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record RefundRequest(
        @NotNull UUID paymentId,
        @Positive long amountCents,
        String reason
) {}
