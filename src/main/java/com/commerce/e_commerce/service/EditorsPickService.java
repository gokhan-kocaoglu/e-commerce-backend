package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.content.EditorsPickRequest;
import com.commerce.e_commerce.dto.content.EditorsPickResponse;

import java.util.List;
import java.util.UUID;

public interface EditorsPickService {
    List<EditorsPickResponse> getAll();
    EditorsPickResponse getById(UUID id);
    EditorsPickResponse getByKey(String key);
    EditorsPickResponse create(EditorsPickRequest req);
    EditorsPickResponse update(UUID id, EditorsPickRequest req);
    void delete(UUID id);
}
