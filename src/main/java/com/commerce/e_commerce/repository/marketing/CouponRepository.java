package com.commerce.e_commerce.repository.marketing;

import com.commerce.e_commerce.domain.marketing.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCodeAndDeletedFalse(String code);
}
