package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.dto.common.MoneyDto;

import java.math.BigDecimal;

public class CommonMapperStatics {
    private CommonMapperStatics() {}

    public static MoneyDto centsToUsd(Long cents) {
        if (cents == null) return null;
        // cents(Long) -> BigDecimal amount (2 ondalık)
        BigDecimal amount = BigDecimal.valueOf(cents, 2);
        return new MoneyDto(amount, "USD");
    }
}
