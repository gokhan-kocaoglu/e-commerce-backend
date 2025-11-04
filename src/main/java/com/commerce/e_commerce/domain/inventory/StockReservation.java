package com.commerce.e_commerce.domain.inventory;

import com.commerce.e_commerce.domain.catalog.ProductVariant;
import com.commerce.e_commerce.domain.common.BaseEntity;
import com.commerce.e_commerce.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="stock_reservation", indexes = @Index(name="idx_sr_order", columnList="order_id"))
@Getter
@Setter
public class StockReservation extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    private Order order;

    @Column(nullable=false) private int reservedQty;
    @Column(nullable=false) private boolean released = false;
}
