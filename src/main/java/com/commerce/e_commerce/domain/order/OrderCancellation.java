package com.commerce.e_commerce.domain.order;

import com.commerce.e_commerce.domain.common.BaseEntity;
import com.commerce.e_commerce.domain.common.enums.CancellationReason;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="order_cancellation", indexes=@Index(name="idx_oc_order", columnList="order_id"))
@Getter
@Setter
public class OrderCancellation extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false) private Order order;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private CancellationReason reason;

    @Column(length=500) private String note;
}
