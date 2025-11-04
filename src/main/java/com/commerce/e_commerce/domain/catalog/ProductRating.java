package com.commerce.e_commerce.domain.catalog;

import com.commerce.e_commerce.domain.common.BaseEntity;
import com.commerce.e_commerce.domain.security.User;
import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;

@Entity
@Table(name = "product_rating",
        uniqueConstraints = @UniqueConstraint(name="uk_product_rating_user",
                columnNames = {"product_id","user_id"}),
        indexes = {
                @Index(name="idx_pr_product", columnList="product_id"),
                @Index(name="idx_pr_user", columnList="user_id")
        })
@Getter @Setter
public class ProductRating extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false)
    private int rating; // 1..5

    @Column(length = 800)
    private String comment; // opsiyonel

}
