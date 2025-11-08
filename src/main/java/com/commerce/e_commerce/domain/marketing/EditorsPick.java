package com.commerce.e_commerce.domain.marketing;

import com.commerce.e_commerce.domain.catalog.Category;
import com.commerce.e_commerce.domain.common.BaseEntity;
import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="editors_pick")
@Getter
@Setter
public class EditorsPick extends SoftDeletable {
    @Column(nullable=false, length=80) private String key = "homepage";

    @ManyToMany
    @JoinTable(name="editors_pick_categories",
            joinColumns=@JoinColumn(name="pick_id"),
            inverseJoinColumns=@JoinColumn(name="category_id"))
    private List<Category> categories = new ArrayList<>();
}
