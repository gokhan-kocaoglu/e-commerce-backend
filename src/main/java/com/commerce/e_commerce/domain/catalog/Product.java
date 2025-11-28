package com.commerce.e_commerce.domain.catalog;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="product",
        indexes = {@Index(name="idx_product_category", columnList="category_id"),
                @Index(name="idx_product_brand", columnList="brand_id")},
        uniqueConstraints=@UniqueConstraint(columnNames="slug"))
@Getter
@Setter
public class Product extends SoftDeletable {
    @Column(nullable=false, length=180) private String title;
    @Column(length=2000) private String description;
    @Column(nullable=false, length=160) private String slug;

    @ManyToOne(fetch = FetchType.LAZY) private Category category;
    @ManyToOne(fetch = FetchType.LAZY) private Brand brand;

    // Listeleme fiyatı (aktif fiyat ProductVariant'ta da olabilir)
    @Column(nullable=false) private Long priceCents;     // integer para (kuruş)
    @Column private Long compareAtPriceCents;            // indirim öncesi

    @OneToMany(mappedBy="product", cascade=CascadeType.ALL, orphanRemoval=true)
    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    private List<ProductImage> images = new ArrayList<>();
}
