package com.commerce.e_commerce.repository.inventory;

import com.commerce.e_commerce.domain.inventory.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {
    Optional<Stock> findByVariantId(UUID variantId);
}
