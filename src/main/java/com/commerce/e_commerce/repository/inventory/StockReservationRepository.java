package com.commerce.e_commerce.repository.inventory;

import com.commerce.e_commerce.domain.inventory.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {
    List<StockReservation> findByOrderId(UUID orderId);
    Optional<StockReservation> findByOrderIdAndVariantIdAndReleasedFalse(UUID orderId, UUID variantId);
}
