package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.dto.content.CampaignRequest;
import com.commerce.e_commerce.dto.content.CampaignResponse;
import com.commerce.e_commerce.service.CampaignAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/content/campaigns")
@RequiredArgsConstructor
public class CampaignAdminController {

    private final CampaignAdminService campaignAdminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CampaignResponse>> create(@RequestBody CampaignRequest req) {
        var created = campaignAdminService.create(req);
        return ResponseEntity.ok(ApiResponse.ok(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> update(@PathVariable UUID id,
                                                                @RequestBody CampaignRequest req) {
        var updated = campaignAdminService.update(id, req);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        campaignAdminService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(campaignAdminService.get(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CampaignResponse>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(campaignAdminService.listAll()));
    }
}

