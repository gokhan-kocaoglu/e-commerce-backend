package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.marketing.Campaign;
import com.commerce.e_commerce.dto.content.CampaignRequest;
import com.commerce.e_commerce.dto.content.CampaignResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.ContentMapper;
import com.commerce.e_commerce.repository.content.CampaignRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class CampaignAdminServiceImpl implements CampaignAdminService {

    private final CampaignRepository campaignRepo;
    private final ContentMapper mapper;

    @Override
    public CampaignResponse create(CampaignRequest req) {
        validateDates(req);
        // title benzersizliği
        if (req.title() != null && campaignRepo.existsByTitleAndDeletedFalse(req.title())) {
            throw new ApiException("CAMPAIGN_TITLE_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
        }

        Campaign c = mapper.toCampaign(req);
        // active null gelirse varsayılanı koru (entity default true) veya explicitly set:
        if (req.active() != null) c.setActive(req.active());
        campaignRepo.save(c);
        return mapper.toCampaignResponse(c);
    }

    @Override
    public CampaignResponse update(UUID id, CampaignRequest req) {
        Campaign c = campaignRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException("CAMPAIGN_NOT_FOUND", HttpStatus.NOT_FOUND));

        validateDates(req);

        // title değişiyorsa çakışma kontrolü
        if (req.title() != null && !req.title().equals(c.getTitle())
                && campaignRepo.existsByTitleAndDeletedFalse(req.title())) {
            throw new ApiException("CAMPAIGN_TITLE_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
        }

        mapper.updateCampaign(c, req);
        return mapper.toCampaignResponse(c);
    }

    @Override
    public void delete(UUID id) {
        Campaign c = campaignRepo.findById(id)
                .orElseThrow(() -> new ApiException("CAMPAIGN_NOT_FOUND", HttpStatus.NOT_FOUND));
        campaignRepo.delete(c); // @SQLDelete => deleted = true
    }

    @Transactional(readOnly = true)
    @Override
    public CampaignResponse get(UUID id) {
        Campaign c = campaignRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException("CAMPAIGN_NOT_FOUND", HttpStatus.NOT_FOUND));
        return mapper.toCampaignResponse(c);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CampaignResponse> listAll() {
        return campaignRepo.findAll().stream()
                .map(mapper::toCampaignResponse)
                .toList();
    }

    // ---- helpers ----
    private void validateDates(CampaignRequest req) {
        if (req.startsAt() != null && req.endsAt() != null && req.endsAt().isBefore(req.startsAt())) {
            throw new ApiException("ENDS_AT_BEFORE_STARTS_AT", HttpStatus.BAD_REQUEST);
        }
    }
}

