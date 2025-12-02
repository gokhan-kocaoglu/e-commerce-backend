package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.dto.inventory.*;
import com.commerce.e_commerce.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryPublicController {

    private final InventoryService inventoryService;

    // PUBLIC: tek varyant stok görüntüleme
    @GetMapping("/variants/{variantId}/stock")
    public ResponseEntity<ApiResponse<StockResponse>> stock(@PathVariable UUID variantId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getStock(variantId)));
    }

    // PUBLIC/ADMIN: hareket geçmişi (istersen ADMIN’e kısıtlayabilirsin)
    @GetMapping("/variants/{variantId}/movements")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> movements(@PathVariable UUID variantId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.movements(variantId)));
    }

    // CHECKOUT: rezervasyon & release & consume
    @PostMapping("/reservations")
    public ResponseEntity<ApiResponse<ReservationResult>> reserve(@RequestBody ReservationRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.reserve(req)));
    }

    @PostMapping("/reservations/{orderId}/release")
    public ResponseEntity<ApiResponse<ReservationResult>> release(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.releaseAll(orderId)));
    }

    @PostMapping("/reservations/{orderId}/consume")
    public ResponseEntity<ApiResponse<ReservationResult>> consume(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.consumeAll(orderId)));
    }
}