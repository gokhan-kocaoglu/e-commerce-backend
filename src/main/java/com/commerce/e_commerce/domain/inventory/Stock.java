package com.commerce.e_commerce.domain.inventory;

import com.commerce.e_commerce.domain.catalog.ProductVariant;
import com.commerce.e_commerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="stock",
        uniqueConstraints=@UniqueConstraint(columnNames={"variant_id"}))
@Getter
@Setter
public class Stock extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="variant_id", unique = true)
    private ProductVariant variant;

    @Column(nullable=false) private int quantityOnHand = 0;     // eldeki
    @Column(nullable=false) private int quantityReserved = 0;   // rezerve
}
