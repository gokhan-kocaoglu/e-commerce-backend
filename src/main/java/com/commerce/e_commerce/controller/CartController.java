package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.catalog.CartCouponRequest;
import com.commerce.e_commerce.dto.catalog.CartItemRequest;
import com.commerce.e_commerce.dto.catalog.CartResponse;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> me(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(cartService.getOrCreate(userId)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> add(Authentication auth,
                                                         @Valid @RequestBody CartItemRequest req) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(cartService.addItem(userId, req)));
    }

    @PutMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> update(Authentication auth,
                                                            @Valid @RequestBody CartItemRequest req) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(cartService.updateItem(userId, req)));
    }

    @DeleteMapping("/items/{variantId}")
    public ResponseEntity<ApiResponse<CartResponse>> remove(Authentication auth,
                                                            @PathVariable UUID variantId) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(cartService.removeItem(userId, variantId)));
    }

    @PostMapping("/coupon")
    public ResponseEntity<ApiResponse<CartResponse>> applyCoupon(Authentication auth,
                                                                 @RequestBody CartCouponRequest req) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(cartService.applyCoupon(userId, req)));
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<ApiResponse<CartResponse>> clearCoupon(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(cartService.clearCoupon(userId)));
    }
}
