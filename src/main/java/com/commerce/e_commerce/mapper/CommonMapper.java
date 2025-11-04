package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.dto.common.MoneyDto;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfig.class)
public interface CommonMapper {
    // cents -> MoneyDto
    default MoneyDto toMoneyDto(Long cents) {
        return cents == null ? null : MoneyDto.tryL(cents);
    }
    // MoneyDto -> cents (genelde request’te MoneyDto yok; ama dursun)
    default Long toCents(MoneyDto money) {
        return (money == null) ? null : money.amount();
    }
}
