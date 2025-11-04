package com.commerce.e_commerce.domain.inventory;

import com.commerce.e_commerce.domain.catalog.ProductVariant;
import com.commerce.e_commerce.domain.common.BaseEntity;
import com.commerce.e_commerce.domain.common.enums.StockMovementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="stock_movement", indexes = @Index(name="idx_sm_variant", columnList="variant_id"))
@Getter
@Setter
public class StockMovement extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    private ProductVariant variant;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private StockMovementType type;

    @Column(nullable=false) private int quantity; // (+/-)
    private String reason;
}
