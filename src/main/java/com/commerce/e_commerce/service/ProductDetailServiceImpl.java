package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.ProductDetail;
import com.commerce.e_commerce.dto.catalog.ProductDetailRequest;
import com.commerce.e_commerce.dto.catalog.ProductDetailResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.ProductDetailMapper;
import com.commerce.e_commerce.repository.catalog.ProductDetailRepository;
import com.commerce.e_commerce.repository.catalog.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductDetailRepository detailRepo;
    private final ProductRepository productRepo;
    private final ProductDetailMapper mapper;

    @Override
    public ProductDetailResponse upsert(ProductDetailRequest req) {
        var product = productRepo.findById(req.productId())
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));

        ProductDetail entity = detailRepo.findByProductIdAndDeletedFalse(product.getId())
                .orElse(null);

        if (entity == null) {
            entity = mapper.toEntity(req);
            entity.setProduct(product);
        } else {
            mapper.updateEntity(entity, req);
        }
        detailRepo.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDetailResponse getByProductId(UUID productId) {
        // YOKSA 404 atma → boş DTO dön
        return detailRepo.findByProductIdAndDeletedFalse(productId)
                .map(mapper::toResponse)
                .orElseGet(() -> new ProductDetailResponse(
                        /* id            */ null,
                        /* productId     */ productId,
                        /* shortSummary  */ "",
                        /* sections      */ java.util.List.of(),
                        /* additionalInfo*/ java.util.Map.of()
                ));
    }

    @Override
    public void deleteByProductId(UUID productId) {
        var entity = detailRepo.findByProductIdAndDeletedFalse(productId)
                .orElseThrow(() -> new ApiException("PRODUCT_DETAIL_NOT_FOUND", HttpStatus.NOT_FOUND));
        detailRepo.delete(entity); // SoftDelete
    }
}
