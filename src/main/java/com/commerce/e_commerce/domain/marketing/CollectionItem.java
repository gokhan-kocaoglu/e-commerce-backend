package com.commerce.e_commerce.domain.marketing;

import com.commerce.e_commerce.domain.catalog.Product;
import com.commerce.e_commerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "collection_item",
        indexes = @Index(name="idx_colitem_collection", columnList="collection_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"collection_id","product_id"})
)
@Getter @Setter
public class CollectionItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Collection collection;
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Product product;
    private int sortOrder = 0;
    private String imageUrl;
}
