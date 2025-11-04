package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.ProductVariant;
import com.commerce.e_commerce.dto.catalog.VariantCreateRequest;
import com.commerce.e_commerce.dto.catalog.VariantResponse;
import com.commerce.e_commerce.dto.catalog.VariantUpdateRequest;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.CatalogMapper;
import com.commerce.e_commerce.repository.catalog.ProductRepository;
import com.commerce.e_commerce.repository.catalog.ProductVariantRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class VariantServiceImpl implements VariantService {

    private final ProductVariantRepository variantRepo;
    private final ProductRepository productRepo;
    private final CatalogMapper mapper;

    @Override
    public VariantResponse create(VariantCreateRequest req) {
        var product = productRepo.findById(req.productId())
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));
        if (variantRepo.existsByProductIdAndSkuAndDeletedFalse(req.productId(), req.sku()))
            throw new ApiException("VARIANT_SKU_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);

        ProductVariant v = mapper.toVariant(req);
        v.setProduct(product);
        variantRepo.save(v);
        return mapper.toVariantResponse(v);
    }

    @Override
    public VariantResponse update(UUID id, VariantUpdateRequest req) {
        var v = variantRepo.findById(id)
                .orElseThrow(() -> new ApiException("VARIANT_NOT_FOUND", HttpStatus.NOT_FOUND));
        mapper.updateVariant(v, req);
        return mapper.toVariantResponse(v);
    }

    @Override
    public void delete(UUID id) {
        var v = variantRepo.findById(id)
                .orElseThrow(() -> new ApiException("VARIANT_NOT_FOUND", HttpStatus.NOT_FOUND));
        variantRepo.delete(v);
    }
}
