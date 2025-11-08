package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.catalog.*;
import com.commerce.e_commerce.dto.catalog.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Mapper(config = MapstructConfig.class, uses = { CommonMapper.class })
public interface CatalogMapper {

    // =======================
    // Category
    // =======================
    CategoryResponse toCategoryResponse(Category entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "parent", ignore = true) // parent serviste resolve edilecek
    Category toCategory(CategoryCreateRequest dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "heroImageUrl", source = "heroImageUrl")
    void updateCategory(@MappingTarget Category entity, CategoryUpdateRequest dto);


    // =======================
    // Brand
    // =======================
    BrandResponse toBrandResponse(Brand entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Brand toBrand(BrandRequest dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "logoUrl", source = "logoUrl")
    void updateBrand(@MappingTarget Brand entity, BrandRequest dto);


    // =======================
    // Product (create / update)
    // =======================
    @Mapping(target = "category", ignore = true) // service set eder
    @Mapping(target = "brand", ignore = true)    // service set eder
    @Mapping(target = "images", ignore = true)   // afterMapping ekler
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    // BigDecimal -> cents
    @Mapping(target = "priceCents", source = "price", qualifiedByName = "bigDecimalToCents")
    @Mapping(target = "compareAtPriceCents", source = "compareAtPrice", qualifiedByName = "bigDecimalToCents")
    Product toProduct(ProductCreateRequest dto);

    @AfterMapping
    default void afterProductCreate(ProductCreateRequest dto, @MappingTarget Product entity) {
        if (dto.imageUrls() != null) {
            int i = 0;
            for (String url : dto.imageUrls()) {
                if (url == null || url.isBlank()) continue;
                var img = new ProductImage();
                img.setProduct(entity);
                img.setUrl(url);
                img.setSortOrder(i++);
                entity.getImages().add(img);
            }
        }
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "slug", source = "slug")
    // BigDecimal -> cents
    @Mapping(target = "priceCents", source = "price", qualifiedByName = "bigDecimalToCents")
    @Mapping(target = "compareAtPriceCents", source = "compareAtPrice", qualifiedByName = "bigDecimalToCents")
    void updateProduct(@MappingTarget Product entity, ProductUpdateRequest dto);
    // categoryId/brandId/imageUrls -> serviste handle


    // =======================
    // Product -> Responses (metrics dahil)
    // =======================
    @Mapping(target = "id", source = "product.id")
    // cents -> MoneyDto(USD)
    @Mapping(target = "price", source = "product.priceCents", qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "compareAtPrice", source = "product.compareAtPriceCents", qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "images", expression = "java(toProductResponseImages(product.getImages()))")
    @Mapping(target = "categoryId", source = "product.category.id")
    @Mapping(target = "brandId", source = "product.brand.id")
    @Mapping(target = "ratingAvg", expression = "java(metrics != null ? metrics.getRatingAvg() : 0.0)")
    @Mapping(target = "ratingCount", expression = "java(metrics != null ? metrics.getRatingCount() : 0)")
    @Mapping(target = "bestsellerScore", expression = "java(metrics != null ? metrics.getBestsellerScore() : 0.0)")
    ProductResponse toProductResponse(Product product, ProductMetrics metrics);

    // Liste öğesi (thumbnail + temel metrikler)
    @Mapping(target = "id", source = "product.id")
    // cents -> MoneyDto(USD)
    @Mapping(target = "price", source = "product.priceCents", qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "compareAtPrice", source = "product.compareAtPriceCents", qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "categoryId", source = "product.category.id")
    @Mapping(target = "ratingAvg", expression = "java(metrics != null ? metrics.getRatingAvg() : 0.0)")
    @Mapping(target = "ratingCount", expression = "java(metrics != null ? metrics.getRatingCount() : 0)")
    @Mapping(target = "bestsellerScore", expression = "java(metrics != null ? metrics.getBestsellerScore() : 0.0)")
    @Mapping(target = "thumbnailUrl", expression = "java(firstImageUrl(product))")
    ProductListItemResponse toProductListItem(Product product, ProductMetrics metrics);

    // metrics null overload'lar
    default ProductResponse toProductResponse(Product product) {
        return toProductResponse(product, null);
    }
    default ProductListItemResponse toProductListItem(Product product) {
        return toProductListItem(product, null);
    }

    // Helper: ilk görsel URL
    default String firstImageUrl(Product p) {
        if (p.getImages() == null || p.getImages().isEmpty()) return null;
        return p.getImages().stream()
                .sorted(Comparator.comparingInt(ProductImage::getSortOrder))
                .map(ProductImage::getUrl)
                .findFirst().orElse(null);
    }

    default java.util.List<ProductResponse.Image> toProductResponseImages(java.util.List<ProductImage> images) {
        if (images == null || images.isEmpty()) return java.util.List.of();
        return images.stream()
                .sorted(java.util.Comparator.comparingInt(ProductImage::getSortOrder))
                .map(img -> new ProductResponse.Image(
                        img.getId(),
                        img.getUrl(),
                        img.getAltText(),
                        img.getSortOrder()
                ))
                .toList();
    }

    // Helper: görsel DTO listesi


    // =======================
    // ProductImage
    // =======================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "product", ignore = true) // service set eder
    ProductImage toProductImage(ProductImageCreateRequest dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "url", source = "url")
    @Mapping(target = "altText", source = "altText")
    @Mapping(target = "sortOrder", source = "sortOrder")
    void updateProductImage(@MappingTarget ProductImage entity, ProductImageUpdateRequest dto);


    // =======================
    // Variant
    // =======================
    @Mapping(target = "product", ignore = true) // service set eder
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
// BigDecimal -> cents
    @Mapping(target = "priceCents", source = "priceCents", qualifiedByName = "bigDecimalToCents")
    @Mapping(target = "compareAtPriceCents", source = "compareAtPriceCents", qualifiedByName = "bigDecimalToCents")
    @Mapping(target = "attributesJson", source = "attributes", qualifiedByName = "mapToJson")
    ProductVariant toVariant(VariantCreateRequest dto);

    @BeanMapping(
            ignoreByDefault = true,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "sku", source = "sku")
// BigDecimal -> cents
    @Mapping(target = "priceCents", source = "priceCents", qualifiedByName = "bigDecimalToCents")
    @Mapping(target = "compareAtPriceCents", source = "compareAtPriceCents", qualifiedByName = "bigDecimalToCents")
    @Mapping(target = "attributesJson", source = "attributes", qualifiedByName = "mapToJson")
    void updateVariant(@MappingTarget ProductVariant entity, VariantUpdateRequest dto);

    // cents -> MoneyDto(USD)
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "priceCents", source = "priceCents", qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "compareAtPriceCents", source = "compareAtPriceCents", qualifiedByName = "centsToUsdMoney")
    @Mapping(target = "attributes", source = "attributesJson", qualifiedByName = "jsonToMap")
    VariantResponse toVariantResponse(ProductVariant entity);

    @Named("mapToJson")
    default String mapToJson(Map<String, String> map) {
        if (map == null) return null;
        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            throw new IllegalArgumentException("attributes serialize failed", e);
        }
    }

    @Named("jsonToMap")
    default Map<String, String> jsonToMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            var type = new TypeReference<Map<String,String>>() {};
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("attributes parse failed", e);
        }
    }


    // =======================
    // Ratings
    // =======================
    default RatingResponse toRatingResponse(ProductRating r) {
        return new RatingResponse(
                r.getProduct().getId(),
                r.getUser().getId(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
