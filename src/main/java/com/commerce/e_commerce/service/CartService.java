package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.catalog.CartCouponRequest;
import com.commerce.e_commerce.dto.catalog.CartItemRequest;
import com.commerce.e_commerce.dto.catalog.CartResponse;

import java.util.UUID;

public interface CartService {
    CartResponse getOrCreate(UUID userId);
    CartResponse addItem(UUID userId, CartItemRequest req);
    CartResponse updateItem(UUID userId, CartItemRequest req);  // quantity set
    CartResponse removeItem(UUID userId, UUID variantId);
    CartResponse applyCoupon(UUID userId, CartCouponRequest req);
    CartResponse clearCoupon(UUID userId);
}
