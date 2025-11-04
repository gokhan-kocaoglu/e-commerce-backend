package com.commerce.e_commerce.dto.marketing;

import java.time.Instant;

public record CouponRequest(
        String code,
        Long amountCents,
        Boolean percentage,
        Instant startsAt,
        Instant endsAt,
        Integer usageLimit
) {}
