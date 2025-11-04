package com.commerce.e_commerce.domain.catalog;


import com.commerce.e_commerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "product_metrics",
        indexes = {
                @Index(name="idx_pm_product", columnList="product_id"),
                @Index(name="idx_pm_bestseller", columnList="bestsellerScore DESC")
        })
@Getter @Setter
@DynamicUpdate
public class ProductMetrics extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="product_id", unique = true, nullable = false)
    private Product product;

    // Rating özetleri
    @Column(nullable = false) private double ratingAvg = 0.0; // 0..5
    @Column(nullable = false) private int ratingCount = 0;

    // Satış metrikleri
    @Column(nullable = false) private long totalSold = 0;       // ömür boyu adet
    @Column(nullable = false) private long soldLast30d = 0;     // son 30 gün

    // Listeleme skoru
    @Column(nullable = false) private double bestsellerScore = 0.0;
}
