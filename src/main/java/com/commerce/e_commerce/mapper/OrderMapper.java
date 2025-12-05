package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.order.Order;
import com.commerce.e_commerce.domain.order.OrderAddressSnapshot;
import com.commerce.e_commerce.domain.order.OrderItem;
import com.commerce.e_commerce.dto.order.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapstructConfig.class, uses = { CommonMapper.class })
public interface OrderMapper {

    @Mapping(target = "items", expression = "java(toOrderLines(order.getItems()))")
    @Mapping(target = "itemsTotal", source = "itemsTotalCents",qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "shipping", source = "shippingCents",qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "discount", source = "discountCents",qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "tax", source = "taxCents",qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "grandTotal", source = "grandTotalCents",qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "shippingAddress", expression = "java(toAddress(order.getShippingAddressJson()))")
    @Mapping(target = "billingAddress",  expression = "java(toAddress(order.getBillingAddressJson()))")
    OrderResponse toOrderResponse(Order order);

    default List<OrderResponse.OrderLine> toOrderLines(List<OrderItem> items) {
        if (items == null) return List.of();
        return items.stream().map(i ->
                new OrderResponse.OrderLine(
                        i.getVariant() != null ? i.getVariant().getId() : null,
                        i.getSkuSnapshot(),
                        i.getProductTitleSnapshot(),
                        i.getQuantity(),
                        CommonMapperStatics.centsToUsd(i.getUnitPriceCents()),
                        CommonMapperStatics.centsToUsd(i.getLineTotalCents()),
                        i.getProductImageUrlSnapshot(),
                        i.getProductImageAltSnapshot()
                )
        ).toList();
    }

    default OrderResponse.Address toAddress(OrderAddressSnapshot s) {
        if (s == null) return null;
        return new OrderResponse.Address(
                s.getFullName(),
                s.getLine1(),
                s.getLine2(),
                s.getCity(),
                s.getState(),
                s.getPostalCode(),
                s.getCountryCode()
        );
    }
}
