package com.commerce.e_commerce.domain.marketing;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="coupon", uniqueConstraints=@UniqueConstraint(columnNames="code"))
@Getter
@Setter
public class Coupon extends SoftDeletable {
    @Column(nullable=false, length=40) private String code;
    @Column(nullable=false) private Long amountCents;  // veya percent
    private boolean percentage;                       // true ise amount %’dir
    private Instant startsAt;
    private Instant endsAt;
    private Integer usageLimit;  // null = sınırsız
    private Integer usedCount = 0;
}
