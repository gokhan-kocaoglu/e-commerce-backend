package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.content.AnnouncementResponse;
import com.commerce.e_commerce.dto.content.CampaignResponse;
import com.commerce.e_commerce.dto.content.EditorsPickResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.ContentMapper;
import com.commerce.e_commerce.repository.content.AnnouncementRepository;
import com.commerce.e_commerce.repository.content.CampaignRepository;
import com.commerce.e_commerce.repository.content.EditorsPickRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional(readOnly = true)
public class ContentServiceImpl implements ContentService {

    private final AnnouncementRepository announcementRepo;
    private final CampaignRepository campaignRepo;
    private final EditorsPickRepository editorsPickRepo;
    private final ContentMapper mapper;

    @Override
    public List<AnnouncementResponse> activeAnnouncements() {
        return announcementRepo.findActive().stream().map(mapper::toAnnouncementResponse).toList();
    }

    @Override
    public List<CampaignResponse> activeCampaigns() {
        return campaignRepo.findActive().stream().map(mapper::toCampaignResponse).toList();
    }

    @Override
    public EditorsPickResponse getEditorsPick(UUID pickId) {
        var e = editorsPickRepo.findById(pickId)
                .orElseThrow(() -> new ApiException("EDITORS_PICK_NOT_FOUND", HttpStatus.NOT_FOUND));
        return mapper.toEditorsPickResponse(e);
    }
}
