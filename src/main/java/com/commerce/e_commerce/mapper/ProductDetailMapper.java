package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.catalog.ProductDetail;
import com.commerce.e_commerce.dto.catalog.ProductDetailRequest;
import com.commerce.e_commerce.dto.catalog.ProductDetailResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;

@Mapper(config = MapstructConfig.class)
public interface ProductDetailMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "product", ignore = true) // service set edecek
    @Mapping(target = "sectionsJson", source = "sections", qualifiedByName = "sectionsToJson")
    @Mapping(target = "additionalInfoJson", source = "additionalInfo", qualifiedByName = "mapToJson")
    ProductDetail toEntity(ProductDetailRequest req);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "shortSummary", source = "shortSummary")
    @Mapping(target = "sectionsJson", source = "sections", qualifiedByName = "sectionsToJson")
    @Mapping(target = "additionalInfoJson", source = "additionalInfo", qualifiedByName = "mapToJson")
    void updateEntity(@MappingTarget ProductDetail e, ProductDetailRequest req);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "shortSummary", source = "shortSummary")
    @Mapping(target = "sections", source = "sectionsJson", qualifiedByName = "jsonToSections")
    @Mapping(target = "additionalInfo", source = "additionalInfoJson", qualifiedByName = "jsonToMap")
    ProductDetailResponse toResponse(ProductDetail e);

    // ----- JSON helpers -----
    @Named("sectionsToJson")
    default String sectionsToJson(List<ProductDetailRequest.Section> sections) {
        try {
            if (sections == null) return null;
            return new ObjectMapper().writeValueAsString(sections);
        } catch (Exception e) {
            throw new IllegalArgumentException("sections serialize failed", e);
        }
    }

    @Named("jsonToSections")
    default List<ProductDetailRequest.Section> jsonToSections(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            var type = new TypeReference<List<ProductDetailRequest.Section>>() {};
            return new ObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("sections parse failed", e);
        }
    }

    @Named("mapToJson")
    default String mapToJson(Map<String, String> map) {
        try {
            if (map == null) return null;
            return new ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            throw new IllegalArgumentException("map serialize failed", e);
        }
    }

    @Named("jsonToMap")
    default Map<String, String> jsonToMap(String json) {
        try {
            if (json == null || json.isBlank()) return Map.of();
            var type = new TypeReference<Map<String, String>>() {};
            return new ObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("map parse failed", e);
        }
    }
}
