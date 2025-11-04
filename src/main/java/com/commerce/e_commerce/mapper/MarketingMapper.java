package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.marketing.Collection;
import com.commerce.e_commerce.domain.marketing.CollectionItem;
import com.commerce.e_commerce.domain.marketing.Coupon;
import com.commerce.e_commerce.dto.marketing.CollectionRequest;
import com.commerce.e_commerce.dto.marketing.CollectionResponse;
import com.commerce.e_commerce.dto.marketing.CouponRequest;
import com.commerce.e_commerce.dto.marketing.CouponResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = MapstructConfig.class)
public interface MarketingMapper {

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
            // product FK service’te resolve edilir
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
}
