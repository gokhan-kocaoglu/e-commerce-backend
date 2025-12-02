package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.cart.Cart;
import com.commerce.e_commerce.domain.cart.CartItem;
import com.commerce.e_commerce.domain.catalog.Product;
import com.commerce.e_commerce.domain.catalog.ProductImage;
import com.commerce.e_commerce.dto.catalog.CartResponse;
import com.commerce.e_commerce.dto.common.MoneyDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(config = MapstructConfig.class, uses = { CommonMapper.class })
public interface CartMapper {

    default CartResponse toCartResponse(
            Cart cart,
            MoneyDto itemsTotal,
            MoneyDto shipping,
            MoneyDto discount,
            MoneyDto tax,
            MoneyDto grandTotal
    ) {
        var lines = (cart == null || cart.getItems() == null)
                ? List.<CartResponse.CartLine>of()
                : cart.getItems().stream().map(this::toCartLine).toList();

        var summary = new CartResponse.PriceSummary(itemsTotal, shipping, discount, tax, grandTotal);
        return new CartResponse(cart != null ? cart.getId() : null, lines, summary);
    }

    default CartResponse.CartLine toCartLine(CartItem i) {
        if (i == null) {
            return new CartResponse.CartLine(
                    null, null, null,null, Map.of(), 0,
                    CommonMapperStatics.centsToUsd(0L),
                    CommonMapperStatics.centsToUsd(0L),
                    null
            );
        }

        var variant = i.getVariant();
        var product = (variant != null) ? variant.getProduct() : null;

        // 1) Snapshot -> fallback entity
        UUID productId = i.getProductIdSnapshot();
        if (productId == null && product != null) {
            productId = product.getId();
        }

        String sku = i.getSkuSnapshot();
        if ((sku == null || sku.isBlank()) && variant != null) {
            sku = variant.getSku();
        }

        String productTitle = i.getProductTitleSnapshot();
        if ((productTitle == null || productTitle.isBlank()) && product != null) {
            productTitle = product.getTitle();
        }

        // 2) Attributes (snapshot JSON -> Map)
        Map<String,String> attributes = parseAttributesSafe(i.getAttributesJsonSnapshot());

        // 3) Thumbnail (ilk görsel - sortOrder’a göre)
        String thumbnail = firstImageUrl(product);

        // 4) Fiyatlar
        long unitCents = i.getUnitPriceCents() != null ? i.getUnitPriceCents() : 0L;
        int qty = Math.max(0, i.getQuantity());
        long lineCents = unitCents * (long) qty;

        return new CartResponse.CartLine(
                productId,
                variant != null ? variant.getId() : null,
                sku,
                productTitle,
                attributes,
                qty,
                CommonMapperStatics.centsToUsd(unitCents),
                CommonMapperStatics.centsToUsd(lineCents),
                thumbnail
        );
    }

    private static String firstImageUrl(Product p) {
        if (p == null || p.getImages() == null || p.getImages().isEmpty()) return null;
        return p.getImages().stream()
                .sorted(Comparator.comparingInt(ProductImage::getSortOrder))
                .map(ProductImage::getUrl)
                .findFirst()
                .orElse(null);
    }

    private static Map<String,String> parseAttributesSafe(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            var type = new TypeReference<Map<String,String>>() {};
            return new ObjectMapper().readValue(json, type);
        } catch (Exception e) {
            return Map.of();
        }
    }
}
