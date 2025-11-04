package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.dto.content.AnnouncementResponse;
import com.commerce.e_commerce.dto.content.CampaignResponse;
import com.commerce.e_commerce.dto.content.EditorsPickResponse;
import com.commerce.e_commerce.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/announcements/active")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> activeAnnouncements() {
        return ResponseEntity.ok(ApiResponse.ok(contentService.activeAnnouncements()));
    }

    @GetMapping("/campaigns/active")
    public ResponseEntity<ApiResponse<List<CampaignResponse>>> activeCampaigns() {
        return ResponseEntity.ok(ApiResponse.ok(contentService.activeCampaigns()));
    }

    @GetMapping("/editors-pick/{id}")
    public ResponseEntity<ApiResponse<EditorsPickResponse>> editorsPick(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(contentService.getEditorsPick(id)));
    }
}
