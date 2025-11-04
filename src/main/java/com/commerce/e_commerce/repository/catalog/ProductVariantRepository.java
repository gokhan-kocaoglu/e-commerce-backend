package com.commerce.e_commerce.repository.catalog;

import com.commerce.e_commerce.domain.catalog.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    Optional<ProductVariant> findByProductIdAndSkuAndDeletedFalse(UUID productId, String sku);
    boolean existsByProductIdAndSkuAndDeletedFalse(UUID productId, String sku);
}
