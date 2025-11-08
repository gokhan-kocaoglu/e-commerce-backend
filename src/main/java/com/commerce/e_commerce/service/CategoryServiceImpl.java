package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.Category;
import com.commerce.e_commerce.dto.catalog.CategoryCreateRequest;
import com.commerce.e_commerce.dto.catalog.CategoryResponse;
import com.commerce.e_commerce.dto.catalog.CategoryUpdateRequest;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.CatalogMapper;
import com.commerce.e_commerce.repository.catalog.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;
    private final CatalogMapper mapper;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse create(CategoryCreateRequest req) {
        if (categoryRepo.existsBySlugAndDeletedFalse(req.slug()))
            throw new ApiException("CATEGORY_SLUG_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);

        Category entity = mapper.toCategory(req);
        if (req.parentId() != null) {
            var parent = categoryRepo.findById(req.parentId())
                    .orElseThrow(() -> new ApiException("CATEGORY_PARENT_NOT_FOUND", HttpStatus.NOT_FOUND));
            entity.setParent(parent);
        }
        categoryRepo.save(entity);
        return mapper.toCategoryResponse(entity);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse update(UUID id, CategoryUpdateRequest req) {
        var entity = categoryRepo.findById(id)
                .orElseThrow(() -> new ApiException("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND));

        mapper.updateCategory(entity, req);
        if (req.parentId() != null) {
            var parent = categoryRepo.findById(req.parentId())
                    .orElseThrow(() -> new ApiException("CATEGORY_PARENT_NOT_FOUND", HttpStatus.NOT_FOUND));
            entity.setParent(parent);
        }
        return mapper.toCategoryResponse(entity);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        var entity = categoryRepo.findById(id)
                .orElseThrow(() -> new ApiException("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND));
        categoryRepo.delete(entity); // soft delete
    }

    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("permitAll()")
    public CategoryResponse get(UUID id) {
        var e = categoryRepo.findById(id)
                .orElseThrow(() -> new ApiException("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND));
        return mapper.toCategoryResponse(e);
    }

    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("permitAll()")
    public Page<CategoryResponse> list(Pageable pageable) {
        return categoryRepo.findAll(pageable).map(mapper::toCategoryResponse);
    }
}
