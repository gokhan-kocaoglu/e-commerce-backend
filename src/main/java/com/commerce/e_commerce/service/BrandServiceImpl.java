package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.Brand;
import com.commerce.e_commerce.dto.catalog.BrandRequest;
import com.commerce.e_commerce.dto.catalog.BrandResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.CatalogMapper;
import com.commerce.e_commerce.repository.catalog.BrandRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepo;
    private final CatalogMapper mapper;

    @Override
    public BrandResponse create(BrandRequest req) {
        if (brandRepo.existsBySlugAndDeletedFalse(req.slug()))
            throw new ApiException("BRAND_SLUG_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);

        Brand b = mapper.toBrand(req);
        brandRepo.save(b);
        return mapper.toBrandResponse(b);
    }

    @Override
    public BrandResponse update(UUID id, BrandRequest req) {
        var b = brandRepo.findById(id)
                .orElseThrow(() -> new ApiException("BRAND_NOT_FOUND", HttpStatus.NOT_FOUND));
        mapper.updateBrand(b, req);
        return mapper.toBrandResponse(b);
    }

    @Override
    public void delete(UUID id) {
        var b = brandRepo.findById(id)
                .orElseThrow(() -> new ApiException("BRAND_NOT_FOUND", HttpStatus.NOT_FOUND));
        brandRepo.delete(b);
    }

    @Transactional(readOnly = true)
    @Override
    public BrandResponse get(UUID id) {
        var b = brandRepo.findById(id)
                .orElseThrow(() -> new ApiException("BRAND_NOT_FOUND", HttpStatus.NOT_FOUND));
        return mapper.toBrandResponse(b);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BrandResponse> list(Pageable pageable) {
        return brandRepo.findAll(pageable).map(mapper::toBrandResponse);
    }
}
