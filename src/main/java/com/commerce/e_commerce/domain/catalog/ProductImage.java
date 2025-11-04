package com.commerce.e_commerce.domain.catalog;

import com.commerce.e_commerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="product_image", indexes=@Index(name="idx_pi_product", columnList="product_id"))
@Getter
@Setter
public class ProductImage extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Product product;
    @Column(nullable=false) private String url;
    private String altText;
    private int sortOrder = 0;
}
