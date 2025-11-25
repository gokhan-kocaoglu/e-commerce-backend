package com.commerce.e_commerce.repository.catalog;

import com.commerce.e_commerce.domain.catalog.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, UUID> {
    Optional<ProductDetail> findByProductIdAndDeletedFalse(UUID productId);
    boolean existsByProductIdAndDeletedFalse(UUID productId);
}
