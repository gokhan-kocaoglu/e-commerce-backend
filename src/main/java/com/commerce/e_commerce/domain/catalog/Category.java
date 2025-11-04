package com.commerce.e_commerce.domain.catalog;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="category",
        indexes = {@Index(name="idx_cat_parent", columnList="parent_id")},
        uniqueConstraints=@UniqueConstraint(columnNames="slug"))
@Getter
@Setter
public class Category extends SoftDeletable {
    @Column(nullable=false, length=120) private String name;
    @Column(nullable=false, length=140) private String slug;
    private String heroImageUrl;

    @ManyToOne(fetch = FetchType.LAZY) private Category parent;
}
