package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.marketing.Coupon;
import com.commerce.e_commerce.repository.marketing.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepo;

    @Override
    public Optional<Coupon> validateCoupon(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        var opt = couponRepo.findByCodeAndDeletedFalse(code.trim());
        if (opt.isEmpty()) return Optional.empty();
        var c = opt.get();

        var now = Instant.now();
        if (c.getStartsAt()!=null && now.isBefore(c.getStartsAt())) return Optional.empty();
        if (c.getEndsAt()!=null && now.isAfter(c.getEndsAt())) return Optional.empty();
        if (c.getUsageLimit()!=null && c.getUsedCount()!=null && c.getUsedCount() >= c.getUsageLimit()) return Optional.empty();

        return Optional.of(c);
    }
}
