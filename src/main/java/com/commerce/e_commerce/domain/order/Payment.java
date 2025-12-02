package com.commerce.e_commerce.domain.order;

import com.commerce.e_commerce.domain.common.BaseEntity;
import com.commerce.e_commerce.domain.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="payment",
        indexes={
                @Index(name="idx_payment_order", columnList="order_id"),
                @Index(name="uq_payment_provider_ref", columnList="providerRef", unique=true)
        })
@Getter
@Setter
public class Payment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Order order;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private PaymentStatus status;

    @Column(nullable=false) private Long amountCents;
    @Column(length=80) private String provider;        // iyzico/stripe/etc.
    @Column(length=140) private String providerRef;    // transaction id
    @Column(columnDefinition="jsonb") private String payload; // ham response
    @Column(columnDefinition="jsonb")
    private String cardSnapshotJson; // {brand,last4,expMonth,expYear,holder}
}
