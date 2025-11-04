package com.commerce.e_commerce.dto.common;

public record MoneyDto(long amount, String currency) {
    public static MoneyDto tryL(long amount) { return new MoneyDto(amount, "TRY"); }
}
