package com.commerce.e_commerce.dto.order;

import com.commerce.e_commerce.dto.common.AddressDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequest(
        @NotEmpty List<OrderItemRequest> items,
        @NotNull AddressDto shippingAddress,
        AddressDto billingAddress,
        String couponCode
) {}
