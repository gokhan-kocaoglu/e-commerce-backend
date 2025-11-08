package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.dto.content.EditorsPickRequest;
import com.commerce.e_commerce.dto.content.EditorsPickResponse;
import com.commerce.e_commerce.service.EditorsPickService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/content/editors-picks")
@RequiredArgsConstructor
public class EditorsPickController {

    private final EditorsPickService service;

    // GET — herkes
    @GetMapping
    public ResponseEntity<ApiResponse<List<EditorsPickResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EditorsPickResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @GetMapping("/by-key/{key}")
    public ResponseEntity<ApiResponse<EditorsPickResponse>> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByKey(key)));
    }

    // ADMIN — create/update/delete
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<EditorsPickResponse>> create(@RequestBody EditorsPickRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.create(req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EditorsPickResponse>> update(@PathVariable UUID id,
                                                                   @RequestBody EditorsPickRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
