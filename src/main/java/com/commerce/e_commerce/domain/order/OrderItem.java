package com.commerce.e_commerce.domain.order;

import com.commerce.e_commerce.domain.catalog.ProductVariant;
import com.commerce.e_commerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="order_item", indexes=@Index(name="idx_oi_order", columnList="order_id"))
@Getter
@Setter
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Order order;
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private ProductVariant variant;

    @Column(nullable=false) private String productTitleSnapshot;
    @Column(nullable=false) private String skuSnapshot;

    @Column(nullable=false) private int quantity;
    @Column(nullable=false) private Long unitPriceCents;
    @Column(nullable=false) private Long lineTotalCents;

    @Column(length = 500)
    private String productImageUrlSnapshot;

    @Column(length = 200)
    private String productImageAltSnapshot;
}
