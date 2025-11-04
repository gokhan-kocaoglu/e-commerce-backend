package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.cart.Cart;
import com.commerce.e_commerce.domain.cart.CartItem;
import com.commerce.e_commerce.domain.security.User;
import com.commerce.e_commerce.dto.catalog.CartCouponRequest;
import com.commerce.e_commerce.dto.catalog.CartItemRequest;
import com.commerce.e_commerce.dto.catalog.CartResponse;
import com.commerce.e_commerce.dto.common.MoneyDto;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.CartMapper;
import com.commerce.e_commerce.repository.cart.CartItemRepository;
import com.commerce.e_commerce.repository.cart.CartRepository;
import com.commerce.e_commerce.repository.catalog.ProductVariantRepository;
import com.commerce.e_commerce.repository.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductVariantRepository variantRepo;
    private final CouponService couponService;
    private final CartMapper mapper;
    private final UserRepository userRepo;

    @Override
    public CartResponse getOrCreate(UUID userId) {
        // Kullanıcı gerçekten var mı?
        if (!userRepo.existsById(userId)) {
            throw new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        var cart = cartRepo.findByUserIdAndDeletedFalse(userId)
                .orElseGet(() -> {
                    var c = new com.commerce.e_commerce.domain.cart.Cart();
                    // Ek select atmadan FK referansı ver
                    c.setUser(userRepo.getReferenceById(userId));
                    return cartRepo.save(c);
                });

        return toResponse(cart, Optional.empty());
    }

    @Override
    public CartResponse addItem(UUID userId, CartItemRequest req) {
        var cart = cartRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException("CART_NOT_FOUND", HttpStatus.NOT_FOUND));

        var variant = variantRepo.findById(req.variantId())
                .orElseThrow(() -> new ApiException("VARIANT_NOT_FOUND", HttpStatus.NOT_FOUND));

        var line = cartItemRepo.findByCartIdAndVariantId(cart.getId(), variant.getId())
                .orElseGet(() -> {
                    var ci = new com.commerce.e_commerce.domain.cart.CartItem();
                    ci.setCart(cart);
                    ci.setVariant(variant);
                    ci.setUnitPriceCents(variant.getPriceCents());
                    ci.setQuantity(0);
                    return ci;
                });

        line.setQuantity(line.getQuantity() + req.quantity());
        cartItemRepo.save(line);

        return toResponse(cart, Optional.empty());
    }

    @Override
    public CartResponse updateItem(UUID userId, CartItemRequest req) {
        var cart = cartRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException("CART_NOT_FOUND", HttpStatus.NOT_FOUND));

        var line = cartItemRepo.findByCartIdAndVariantId(cart.getId(), req.variantId())
                .orElseThrow(() -> new ApiException("CART_ITEM_NOT_FOUND", HttpStatus.NOT_FOUND));

        line.setQuantity(req.quantity());
        return toResponse(cart, Optional.empty());
    }

    @Override
    public CartResponse removeItem(UUID userId, UUID variantId) {
        var cart = cartRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException("CART_NOT_FOUND", HttpStatus.NOT_FOUND));

        var line = cartItemRepo.findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new ApiException("CART_ITEM_NOT_FOUND", HttpStatus.NOT_FOUND));

        cartItemRepo.delete(line);
        return toResponse(cart, Optional.empty());
    }

    @Override
    public CartResponse applyCoupon(UUID userId, CartCouponRequest req) {
        var cart = cartRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException("CART_NOT_FOUND", HttpStatus.NOT_FOUND));
        return toResponse(cart, Optional.ofNullable(req.couponCode()));
    }

    @Override
    public CartResponse clearCoupon(UUID userId) {
        var cart = cartRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException("CART_NOT_FOUND", HttpStatus.NOT_FOUND));
        return toResponse(cart, Optional.empty());
    }

    // ---- helpers

    private CartResponse toResponse(com.commerce.e_commerce.domain.cart.Cart cart,
                                    Optional<String> couponCodeOpt) {
        long itemsTotal = cart.getItems().stream()
                .mapToLong(ci -> (long) ci.getUnitPriceCents() * ci.getQuantity())
                .sum();

        long shipping = itemsTotal >= 50000 ? 0 : 2990;
        long discount = 0;

        if (couponCodeOpt.isPresent() && !couponCodeOpt.get().isBlank()) {
            var valid = couponService.validateCoupon(couponCodeOpt.get());
            if (valid.isPresent()) {
                var c = valid.get();
                discount = c.isPercentage()
                        ? Math.round(itemsTotal * (c.getAmountCents() / 100.0))
                        : c.getAmountCents();
            }
        }

        long tax = Math.round(itemsTotal * 0.18);
        long grand = itemsTotal + shipping - discount + tax;

        return mapper.toCartResponse(
                cart,
                MoneyDto.tryL(itemsTotal),
                MoneyDto.tryL(shipping),
                MoneyDto.tryL(discount),
                MoneyDto.tryL(tax),
                MoneyDto.tryL(grand)
        );
    }
}
