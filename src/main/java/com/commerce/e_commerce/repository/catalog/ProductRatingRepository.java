package com.commerce.e_commerce.repository.catalog;

import com.commerce.e_commerce.domain.catalog.ProductRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRatingRepository extends JpaRepository<ProductRating, UUID> {
    Optional<ProductRating> findByProductIdAndUserId(UUID productId, UUID userId);
    @Query("select coalesce(avg(r.rating),0) from ProductRating r where r.product.id=:pid")
    double avgByProduct(@Param("pid") UUID productId);
    @Query("select count(r) from ProductRating r where r.product.id=:pid")
    int countByProduct(@Param("pid") UUID productId);
}
