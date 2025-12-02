package com.commerce.e_commerce.repository.inventory;

import com.commerce.e_commerce.domain.inventory.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findByVariantIdOrderByCreatedAtDesc(UUID variantId);
}