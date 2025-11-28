package com.commerce.e_commerce.domain.cart;

import com.commerce.e_commerce.domain.catalog.ProductVariant;
import com.commerce.e_commerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cart_item", indexes=@Index(name="idx_ci_cart", columnList="cart_id"))
@Getter
@Setter
public class CartItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Cart cart;
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private ProductVariant variant;
    @Column(nullable=false) private int quantity;
    @Column(nullable=false) private Long unitPriceCents; // o anki fiyat
    @Column(length=120) private String skuSnapshot;
    @Column(length=200) private String productTitleSnapshot;
    @Column(length=400) private String attributesJsonSnapshot;
}
