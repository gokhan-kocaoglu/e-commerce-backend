package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.content.CampaignRequest;
import com.commerce.e_commerce.dto.content.CampaignResponse;

import java.util.List;
import java.util.UUID;

public interface CampaignAdminService {
    CampaignResponse create(CampaignRequest req);
    CampaignResponse update(UUID id, CampaignRequest req);
    void delete(UUID id);
    CampaignResponse get(UUID id);
    List<CampaignResponse> listAll();
}

