// CartServiceImpl.java
package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.cart.Cart;
import com.commerce.e_commerce.domain.cart.CartItem;
import com.commerce.e_commerce.dto.catalog.CartCouponRequest;
import com.commerce.e_commerce.dto.catalog.CartItemRequest;
import com.commerce.e_commerce.dto.catalog.CartResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.CartMapper;
import com.commerce.e_commerce.mapper.CommonMapperStatics;
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

    // Tek giriş noktası: kullanıcı sepetini yükle, yoksa oluştur (fetch-join'li)
    private Cart loadCartWithItems(UUID userId) {
        if (userId == null) throw new ApiException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        if (!userRepo.existsById(userId)) throw new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND);

        return cartRepo.findWithItemsByUserIdAndDeletedFalse(userId)
                .orElseGet(() -> {
                    var c = new Cart();
                    c.setUser(userRepo.getReferenceById(userId));
                    return cartRepo.save(c);
                });
    }

    // Ekleme/silme/update sonrası taze state
    private Cart reloadCart(UUID userId) {
        return cartRepo.findWithItemsByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ApiException("CART_NOT_FOUND", HttpStatus.NOT_FOUND));
    }

    @Override
    public CartResponse getOrCreate(UUID userId) {
        var cart = loadCartWithItems(userId);
        return toResponse(cart, Optional.empty());
    }

    @Override
    public CartResponse addItem(UUID userId, CartItemRequest req) {
        if (req.quantity() <= 0) throw new ApiException("INVALID_QUANTITY", HttpStatus.BAD_REQUEST);

        var cart = loadCartWithItems(userId);
        var variant = variantRepo.findById(req.variantId())
                .orElseThrow(() -> new ApiException("VARIANT_NOT_FOUND", HttpStatus.NOT_FOUND));

        var lineOpt = cartItemRepo.findByCartIdAndVariantId(cart.getId(), variant.getId());
        CartItem line;
        if (lineOpt.isPresent()) {
            line = lineOpt.get();
        } else {
            line = new CartItem();
            line.setCart(cart);
            line.setVariant(variant);

            // snapshot
            line.setUnitPriceCents(variant.getPriceCents());
            line.setSkuSnapshot(variant.getSku());
            line.setProductTitleSnapshot(variant.getProduct().getTitle());
            line.setAttributesJsonSnapshot(variant.getAttributesJson());

            line.setQuantity(0);

            // Koleksiyonu senkron tut
            cart.getItems().add(line);
        }

        line.setQuantity(line.getQuantity() + req.quantity());
        cartItemRepo.save(line);

        // Taze state ile dön
        return toResponse(reloadCart(userId), Optional.empty());
    }

    @Override
    public CartResponse updateItem(UUID userId, CartItemRequest req) {
        if (req.quantity() < 0) throw new ApiException("INVALID_QUANTITY", HttpStatus.BAD_REQUEST);

        var cart = loadCartWithItems(userId);
        var line = cartItemRepo.findByCartIdAndVariantId(cart.getId(), req.variantId())
                .orElseThrow(() -> new ApiException("CART_ITEM_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (req.quantity() == 0) {
            cart.getItems().remove(line);  // koleksiyondan da çıkar
            cartItemRepo.delete(line);
        } else {
            line.setQuantity(req.quantity());
        }

        return toResponse(reloadCart(userId), Optional.empty());
    }

    @Override
    public CartResponse removeItem(UUID userId, UUID variantId) {
        var cart = loadCartWithItems(userId);
        var line = cartItemRepo.findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new ApiException("CART_ITEM_NOT_FOUND", HttpStatus.NOT_FOUND));

        cart.getItems().remove(line);  // koleksiyon senkron
        cartItemRepo.delete(line);

        return toResponse(reloadCart(userId), Optional.empty());
    }

    @Override
    public CartResponse applyCoupon(UUID userId, CartCouponRequest req) {
        var cart = loadCartWithItems(userId);
        return toResponse(cart, Optional.ofNullable(req.couponCode()));
    }

    @Override
    public CartResponse clearCoupon(UUID userId) {
        var cart = loadCartWithItems(userId);
        return toResponse(cart, Optional.empty());
    }

    // Hesaplama / Response
    private CartResponse toResponse(Cart cart, Optional<String> couponCodeOpt) {
        long itemsTotal = cart.getItems().stream()
                .mapToLong(ci -> (long) ci.getUnitPriceCents() * ci.getQuantity())
                .sum();

        long shipping = itemsTotal >= 50000 ? 0 : 2990; // örnek kural
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
                CommonMapperStatics.centsToUsd(itemsTotal),
                CommonMapperStatics.centsToUsd(shipping),
                CommonMapperStatics.centsToUsd(discount),
                CommonMapperStatics.centsToUsd(tax),
                CommonMapperStatics.centsToUsd(grand)
        );
    }
}
