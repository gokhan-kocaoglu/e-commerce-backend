package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.Category;
import com.commerce.e_commerce.domain.marketing.EditorsPick;
import com.commerce.e_commerce.dto.content.EditorsPickRequest;
import com.commerce.e_commerce.dto.content.EditorsPickResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.MarketingMapper;
import com.commerce.e_commerce.repository.catalog.CategoryRepository;
import com.commerce.e_commerce.repository.content.EditorsPickRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class EditorsPickServiceImpl implements EditorsPickService {

    private final EditorsPickRepository editorsPickRepo;
    private final CategoryRepository categoryRepo;
    private final MarketingMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<EditorsPickResponse> getAll() {
        // Entity'de @Where(deleted=false) olduğu için findAll() zaten aktif kayıtları getirir
        var list = editorsPickRepo.findAll();
        return mapper.toEditorsPickResponseList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public EditorsPickResponse getById(UUID id) {
        var e = editorsPickRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException("EDITORS_PICK_NOT_FOUND", HttpStatus.NOT_FOUND));
        return mapper.toEditorsPickResponse(e);
    }

    @Override
    @Transactional(readOnly = true)
    public EditorsPickResponse getByKey(String key) {
        var e = editorsPickRepo.findByKeyAndDeletedFalse(key)
                .orElseThrow(() -> new ApiException("EDITORS_PICK_NOT_FOUND", HttpStatus.NOT_FOUND));
        return mapper.toEditorsPickResponse(e);
    }

    @Override
    public EditorsPickResponse create(EditorsPickRequest req) {
        // key benzersizliği (opsiyonel)
        if (req.key() != null && editorsPickRepo.existsByKeyAndDeletedFalse(req.key())) {
            throw new ApiException("EDITORS_PICK_KEY_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
        }

        EditorsPick e = mapper.toEditorsPick(req);
        e.setCategories(resolveCategories(req.categoryIds()));
        editorsPickRepo.save(e);

        return mapper.toEditorsPickResponse(e);
    }

    @Override
    public EditorsPickResponse update(UUID id, EditorsPickRequest req) {
        var e = editorsPickRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException("EDITORS_PICK_NOT_FOUND", HttpStatus.NOT_FOUND));

        // key değişiyorsa çakışma kontrolü
        if (req.key() != null && !req.key().equals(e.getKey())
                && editorsPickRepo.existsByKeyAndDeletedFalse(req.key())) {
            throw new ApiException("EDITORS_PICK_KEY_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
        }

        mapper.updateEditorsPick(e, req);                 // key güncellemesi burada
        e.setCategories(resolveCategories(req.categoryIds())); // kategori set'ini komple yenile

        editorsPickRepo.save(e);
        return mapper.toEditorsPickResponse(e);
    }

    @Override
    public void delete(UUID id) {
        var e = editorsPickRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ApiException("EDITORS_PICK_NOT_FOUND", HttpStatus.NOT_FOUND));
        editorsPickRepo.delete(e); // @SQLDelete => deleted=true
    }

    // ---- helpers
    private List<Category> resolveCategories(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();

        // Category de SoftDeletable ve @Where(deleted=false) ise,
        // findAllById sadece aktif olanları döndürür.
        var found = categoryRepo.findAllById(ids);

        // istenen ID sayısı ile bulunan aktif kategori sayısı uyuşmalı
        // (eksik varsa silinmiş ya da olmayan id vardır)
        // Tekilleştirerek karşılaştırma yapmak daha adil olur:
        int requestedDistinct = new HashSet<>(ids).size();
        if (found.size() != requestedDistinct) {
            throw new ApiException("ONE_OR_MORE_CATEGORIES_NOT_FOUND", HttpStatus.BAD_REQUEST);
        }

        return new ArrayList<>(found);
    }
}