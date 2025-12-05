package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.catalog.Category;
import com.commerce.e_commerce.domain.marketing.Collection;
import com.commerce.e_commerce.domain.marketing.CollectionItem;
import com.commerce.e_commerce.domain.marketing.Coupon;
import com.commerce.e_commerce.domain.marketing.EditorsPick;
import com.commerce.e_commerce.dto.content.EditorsPickRequest;
import com.commerce.e_commerce.dto.content.EditorsPickResponse;
import com.commerce.e_commerce.dto.marketing.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = MapstructConfig.class)
public interface MarketingMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "categories", ignore = true) // IDs -> entities service’te resolve
    EditorsPick toEditorsPick(EditorsPickRequest req);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "key", source = "key")
    @Mapping(target = "categories", ignore = true) // güncellemede de service resolve
    void updateEditorsPick(@MappingTarget EditorsPick e, EditorsPickRequest req);

    // Entity -> Response (categoryIds’i biz set edeceğiz)
    default EditorsPickResponse toEditorsPickResponse(EditorsPick e) {
        var ids = (e.getCategories() == null)
                ? java.util.List.<java.util.UUID>of()
                : e.getCategories().stream()
                .map(Category::getId)
                .toList();
        return new EditorsPickResponse(e.getId(), e.getKey(), ids);
        // record: (UUID id, String key, List<UUID> categoryIds)
    }

    @IterableMapping(elementTargetType = EditorsPickResponse.class)
    java.util.List<EditorsPickResponse> toEditorsPickResponseList(java.util.List<EditorsPick> src);

    // Collection
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "items", ignore = true) // afterMapping
    Collection toCollection(CollectionRequest req);

    @IterableMapping(elementTargetType = CollectionResponse.class)
    List<CollectionResponse> toCollectionResponseList(List<Collection> src);

    @AfterMapping
    default void afterCollection(CollectionRequest req, @MappingTarget Collection entity) {
        if (req.items() == null) return;
        for (var it : req.items()) {
            var ci = new CollectionItem();
            ci.setCollection(entity);
            ci.setSortOrder(it.sortOrder());
            ci.setImageUrl(it.imageUrl());
            // product FK service’te resolve et
            entity.getItems().add(ci);
        }
    }

    default CollectionResponse toCollectionResponse(Collection e) {
        var items = e.getItems()==null ? List.<CollectionResponse.CollectionItem>of()
                : e.getItems().stream()
                .sorted((a,b)-> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(ci -> new CollectionResponse.CollectionItem(
                        ci.getProduct()!=null ? ci.getProduct().getId() : null,
                        ci.getSortOrder(),
                        ci.getImageUrl()
                )).toList();

        return new CollectionResponse(
                e.getId(), e.getName(), e.getSlug(), e.getShortDescription(),
                e.getCtaText(), e.getHeroImageUrl(), items
        );
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "shortDescription", source = "shortDescription")
    @Mapping(target = "ctaText", source = "ctaText")
    @Mapping(target = "heroImageUrl", source = "heroImageUrl")
    void updateCollection(@MappingTarget Collection e, CollectionRequest req);


    // Coupon
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    Coupon toCoupon(CouponRequest req);

    default CouponResponse toCouponResponse(Coupon c) {
        return new CouponResponse(
                c.getId(), c.getCode(),
                c.getAmountCents(), c.isPercentage(),
                c.getStartsAt(), c.getEndsAt(),
                c.getUsageLimit(), c.getUsedCount()
        );
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "code", source = "code")
    @Mapping(target = "amountCents", source = "amountCents")
    @Mapping(target = "percentage", source = "percentage")
    @Mapping(target = "startsAt", source = "startsAt")
    @Mapping(target = "endsAt", source = "endsAt")
    @Mapping(target = "usageLimit", source = "usageLimit")
    void updateCoupon(@MappingTarget Coupon e, CouponRequest req);

    default CollectionSummaryResponse toCollectionSummary(com.commerce.e_commerce.domain.marketing.Collection e, long itemCount) {
        return new CollectionSummaryResponse(
                e.getId(),
                e.getName(),
                e.getSlug(),
                e.getShortDescription(),
                e.getCtaText(),
                e.getHeroImageUrl(),
                itemCount
        );
    }

    default CollectionItemResponse toCollectionItemResponse(com.commerce.e_commerce.domain.marketing.CollectionItem ci) {
        return new CollectionItemResponse(
                ci.getId(),
                ci.getProduct() != null ? ci.getProduct().getId() : null,
                ci.getSortOrder(),
                ci.getImageUrl()
        );
    }
}
