package com.commerce.e_commerce.domain.catalog;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "product_detail")
@Getter
@Setter
public class ProductDetail extends SoftDeletable {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true)
    private Product product;

    // Ürün kartında kısa spot (ör: 2-3 cümle)
    @Column(length = 1000)
    private String shortSummary;

    // “Description” sekmesindeki içerik bölümleri (başlık + metin + maddeler)
    @Column(name = "sections_json",columnDefinition="jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String sectionsJson;

    // “Additional Information” (kargo, iade, bakım, malzeme vs)
    @Column(name = "additional_info_json",columnDefinition="jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String additionalInfoJson;
}
