package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.order.Order;
import com.commerce.e_commerce.domain.order.OrderItem;
import com.commerce.e_commerce.dto.common.MoneyDto;
import com.commerce.e_commerce.dto.order.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapstructConfig.class, uses = { CommonMapper.class })
public interface OrderMapper {

    @Mapping(target = "items", expression = "java(toOrderLines(order.getItems()))")
    @Mapping(target = "itemsTotal", source = "itemsTotalCents")
    @Mapping(target = "shipping", source = "shippingCents")
    @Mapping(target = "discount", source = "discountCents")
    @Mapping(target = "tax", source = "taxCents")
    @Mapping(target = "grandTotal", source = "grandTotalCents")
    OrderResponse toOrderResponse(Order order);

    default List<OrderResponse.OrderLine> toOrderLines(List<OrderItem> items) {
        if (items == null) return List.of();
        return items.stream().map(i ->
                new OrderResponse.OrderLine(
                        i.getVariant().getId(),
                        i.getSkuSnapshot(),
                        i.getProductTitleSnapshot(),
                        i.getQuantity(),
                        MoneyDto.tryL(i.getUnitPriceCents()),
                        MoneyDto.tryL(i.getLineTotalCents())
                )
        ).toList();
    }
}
