package com.commerce.e_commerce.domain.catalog;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="product_variant",
        uniqueConstraints=@UniqueConstraint(columnNames={"product_id","sku"}))
@Getter
@Setter
public class ProductVariant extends SoftDeletable {
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Product product;
    @Column(nullable=false, length=80) private String sku;
    @Column(nullable=false) private Long priceCents;
    @Column private Long compareAtPriceCents;
    @Column(length=400) private String attributesJson; // {size:"M", color:"Black"} gibi
}
