package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.dto.common.MoneyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(config = MapstructConfig.class)
public interface CommonMapper {

    @Named("bigDecimalToCents")
    default Long bigDecimalToCents(BigDecimal amount) {
        if (amount == null) return null;
        return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    @Named("centsToBigDecimal")
    default BigDecimal centsToBigDecimal(Long cents) {
        if (cents == null) return null;
        return new BigDecimal(cents).movePointLeft(2);
    }

    // Response için USD
    @Named("centsToUsdMoney")
    default MoneyDto centsToUsdMoney(Long cents) {
        if (cents == null) return null;
        return new MoneyDto(centsToBigDecimal(cents), "USD");
    }
}
