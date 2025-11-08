package com.commerce.e_commerce.dto.common;

import java.math.BigDecimal;

public record MoneyDto(
        BigDecimal amount,
        String currency
) {
    public static MoneyDto usd(BigDecimal amount) {
        return new MoneyDto(amount, "USD");
    }
}
