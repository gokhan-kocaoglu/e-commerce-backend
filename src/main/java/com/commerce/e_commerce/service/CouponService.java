package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.marketing.Coupon;

import java.util.Optional;

public interface CouponService {
    Optional<Coupon> validateCoupon(String code);
}
