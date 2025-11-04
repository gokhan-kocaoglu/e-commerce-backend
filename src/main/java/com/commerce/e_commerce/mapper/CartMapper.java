package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.cart.Cart;
import com.commerce.e_commerce.domain.cart.CartItem;
import com.commerce.e_commerce.domain.catalog.ProductImage;
import com.commerce.e_commerce.dto.catalog.CartResponse;
import com.commerce.e_commerce.dto.common.MoneyDto;
import org.mapstruct.Mapper;

import java.util.Comparator;
import java.util.List;

@Mapper(config = MapstructConfig.class, uses = { CommonMapper.class })
public interface CartMapper {

    default CartResponse toCartResponse(Cart cart, MoneyDto itemsTotal, MoneyDto shipping, MoneyDto discount, MoneyDto tax, MoneyDto grandTotal) {
        var lines = cart.getItems() == null ? List.<CartResponse.CartLine>of()
                : cart.getItems().stream().map(this::toCartLine).toList();
        var summary = new CartResponse.PriceSummary(itemsTotal, shipping, discount, tax, grandTotal);
        return new CartResponse(cart.getId(), lines, summary);
    }

    default CartResponse.CartLine toCartLine(CartItem i) {
        var variant = i.getVariant();
        var product = variant.getProduct();
        String thumbnail = null;
        if (product.getImages()!=null && !product.getImages().isEmpty()) {
            thumbnail = product.getImages().stream()
                    .sorted(Comparator.comparingInt(ProductImage::getSortOrder))
                    .map(ProductImage::getUrl).findFirst().orElse(null);
        }
        return new CartResponse.CartLine(
                variant.getId(),
                variant.getSku(),
                product.getTitle(),
                i.getQuantity(),
                MoneyDto.tryL(i.getUnitPriceCents()),
                MoneyDto.tryL((long) i.getUnitPriceCents() * i.getQuantity()),
                thumbnail
        );
    }
}
