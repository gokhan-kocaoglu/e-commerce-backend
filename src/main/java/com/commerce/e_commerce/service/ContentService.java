package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.content.AnnouncementResponse;
import com.commerce.e_commerce.dto.content.CampaignResponse;
import com.commerce.e_commerce.dto.content.EditorsPickResponse;

import java.util.List;
import java.util.UUID;

public interface ContentService {
    List<AnnouncementResponse> activeAnnouncements();
    List<CampaignResponse> activeCampaigns();
    EditorsPickResponse getEditorsPick(UUID pickId);


}
