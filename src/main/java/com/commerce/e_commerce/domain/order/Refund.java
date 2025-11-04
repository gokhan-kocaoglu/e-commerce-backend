package com.commerce.e_commerce.domain.order;

import com.commerce.e_commerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="refund", indexes=@Index(name="idx_refund_payment", columnList="payment_id"))
@Getter
@Setter
public class Refund extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Payment payment;
    @Column(nullable=false) private Long amountCents;
    private String reason;
}
