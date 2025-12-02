package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.inventory.*;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    StockResponse getStock(UUID variantId);
    List<StockMovementResponse> movements(UUID variantId);

    // Admin
    StockResponse adjust(StockAdjustRequest req);
    StockResponse setOnHand(StockSetRequest req);

    // Rezervasyon akışı (checkout)
    ReservationResult reserve(ReservationRequest req);   // sepet->checkout
    ReservationResult releaseAll(UUID orderId);          // ödeme başarısız/iptal
    ReservationResult consumeAll(UUID orderId);          // ödeme başarılı -> outbound
}