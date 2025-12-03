package com.commerce.e_commerce.domain.order;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import com.commerce.e_commerce.domain.common.enums.OrderStatus;
import com.commerce.e_commerce.domain.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders",
        indexes = {@Index(name="idx_order_user", columnList="user_id"),
                @Index(name="idx_order_status", columnList="status")})
@Getter
@Setter
public class Order extends SoftDeletable {
    @ManyToOne(fetch = FetchType.LAZY) private User user;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private OrderStatus status = OrderStatus.CREATED;

    // özet parasal alanlar
    @Column(nullable=false) private Long itemsTotalCents;
    @Column(nullable=false) private Long shippingCents;
    @Column(nullable=false) private Long discountCents;
    @Column(nullable=false) private Long taxCents;
    @Column(nullable=false) private Long grandTotalCents;

    // snapshot adresler
    // snapshot adresler (jsonb)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private OrderAddressSnapshot shippingAddressJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private OrderAddressSnapshot billingAddressJson;

    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<OrderItem> items = new ArrayList<>();
}
