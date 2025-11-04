package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.marketing.Announcement;
import com.commerce.e_commerce.domain.marketing.Campaign;
import com.commerce.e_commerce.domain.marketing.EditorsPick;
import com.commerce.e_commerce.dto.content.*;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.UUID;

@Mapper(config = MapstructConfig.class)
public interface ContentMapper {

    // Announcement
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Announcement toAnnouncement(AnnouncementRequest req);

    AnnouncementResponse toAnnouncementResponse(Announcement e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "text", source = "text")
    @Mapping(target = "startsAt", source = "startsAt")
    @Mapping(target = "endsAt", source = "endsAt")
    @Mapping(target = "active", source = "active")
    void updateAnnouncement(@MappingTarget Announcement e, AnnouncementRequest req);


    // EditorsPick
    default EditorsPickResponse toEditorsPickResponse(EditorsPick e) {
        List<UUID> ids = e.getCategories().stream().map(c -> c.getId()).toList();
        return new EditorsPickResponse(e.getId(), e.getKey(), ids);
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "key", source = "key")
    void updateEditorsPick(@MappingTarget EditorsPick e, EditorsPickRequest req);


    // Campaign (2. slider)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Campaign toCampaign(CampaignRequest req);

    CampaignResponse toCampaignResponse(Campaign e);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "subtitle", source = "subtitle")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "ctaText", source = "ctaText")
    @Mapping(target = "ctaLink", source = "ctaLink")
    @Mapping(target = "startsAt", source = "startsAt")
    @Mapping(target = "endsAt", source = "endsAt")
    @Mapping(target = "active", source = "active")
    void updateCampaign(@MappingTarget Campaign e, CampaignRequest req);
}
