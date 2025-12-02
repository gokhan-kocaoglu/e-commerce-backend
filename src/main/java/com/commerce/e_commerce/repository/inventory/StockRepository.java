package com.commerce.e_commerce.repository.inventory;

import com.commerce.e_commerce.domain.inventory.Stock;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {

    Optional<Stock> findByVariantId(UUID variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.variant.id = :variantId")
    Optional<Stock> lockByVariantId(@Param("variantId") UUID variantId);
}