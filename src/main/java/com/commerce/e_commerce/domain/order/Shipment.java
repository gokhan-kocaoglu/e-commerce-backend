package com.commerce.e_commerce.domain.order;

import com.commerce.e_commerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="shipment", indexes=@Index(name="idx_shipment_order", columnList="order_id"))
@Getter
@Setter
public class Shipment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Order order;
    @Column(nullable=false, length=80) private String carrier;   // Yurtiçi/Aras...
    @Column(length=120) private String trackingNumber;
    @Column(columnDefinition="jsonb") private String addressJson;
    private Instant shippedAt;
    private Instant deliveredAt;
}
