package com.commerce.e_commerce.domain.marketing;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="collection", uniqueConstraints=@UniqueConstraint(columnNames="slug"))
@Getter
@Setter
public class Collection extends SoftDeletable {
    @Column(nullable=false, length=140) private String name;           // "New Koleksiyon"
    @Column(nullable=false, length=160) private String slug;           // "new-collection"
    @Column(length=280) private String shortDescription;               // küçük açıklama
    @Column(length=80)  private String ctaText;                        // buton üstü yazı
    private String heroImageUrl;                                       // koleksiyon görseli

    @OneToMany(mappedBy="collection", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<CollectionItem> items = new ArrayList<>();
}
