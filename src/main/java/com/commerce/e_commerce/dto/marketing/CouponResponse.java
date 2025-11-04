package com.commerce.e_commerce.dto.marketing;

import java.time.Instant;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        long amountCents,
        boolean percentage,
        Instant startsAt,
        Instant endsAt,
        Integer usageLimit,
        Integer usedCount
) {}
