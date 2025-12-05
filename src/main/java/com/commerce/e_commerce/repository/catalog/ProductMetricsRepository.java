package com.commerce.e_commerce.repository.catalog;

import com.commerce.e_commerce.domain.catalog.ProductMetrics;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductMetricsRepository extends JpaRepository<ProductMetrics, UUID> {
    Optional<ProductMetrics> findByProductId(UUID productId);

    // pessimistic lock (yüksek eşzamanlılık için)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pm from ProductMetrics pm where pm.product.id=:pid")
    Optional<ProductMetrics> findByProductIdForUpdate(@Param("pid") UUID productId);

    @Query("""
           select pm
           from ProductMetrics pm
           join pm.product p
           where p.deleted = false
           order by pm.bestsellerScore desc
           """)
    Page<ProductMetrics> findTopByBestseller(Pageable pageable);

    @Query("""
        select pm
        from ProductMetrics pm
        join pm.product p
        where p.deleted = false
          and p.category.id = :categoryId
        order by pm.bestsellerScore desc, pm.ratingAvg desc
    """)
    Page<ProductMetrics> findTopByBestsellerInCategory(@Param("categoryId") UUID categoryId, Pageable pageable);
}
