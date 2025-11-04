package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.ProductMetrics;
import com.commerce.e_commerce.dto.catalog.ProductCreateRequest;
import com.commerce.e_commerce.dto.catalog.ProductListItemResponse;
import com.commerce.e_commerce.dto.catalog.ProductResponse;
import com.commerce.e_commerce.dto.catalog.ProductUpdateRequest;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.CatalogMapper;
import com.commerce.e_commerce.repository.catalog.BrandRepository;
import com.commerce.e_commerce.repository.catalog.CategoryRepository;
import com.commerce.e_commerce.repository.catalog.ProductMetricsRepository;
import com.commerce.e_commerce.repository.catalog.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final BrandRepository brandRepo;
    private final ProductMetricsRepository metricsRepo;
    private final CatalogMapper mapper;

    @Override
    public ProductResponse create(ProductCreateRequest req) {
        var p = mapper.toProduct(req);
        p.setCategory(categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new ApiException("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND)));
        if (req.brandId() != null) {
            var b = brandRepo.findById(req.brandId())
                    .orElseThrow(() -> new ApiException("brand_not_found", HttpStatus.NOT_FOUND));
            p.setBrand(b);
        }
        productRepo.save(p);

        // Metrics 1-1 oluştur (ratingAvg=0, sold=0)
        var pm = new ProductMetrics();
        pm.setProduct(p);
        metricsRepo.save(pm);

        return mapper.toProductResponse(p);
    }

    @Override
    public ProductResponse update(UUID id, ProductUpdateRequest req) {
        var p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));
        mapper.updateProduct(p, req);

        if (req.categoryId() != null) {
            p.setCategory(categoryRepo.findById(req.categoryId())
                    .orElseThrow(() -> new ApiException("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND)));
        }
        if (req.brandId() != null) {
            p.setBrand(brandRepo.findById(req.brandId())
                    .orElseThrow(() -> new ApiException("BRAND_NOT_FOUND", HttpStatus.NOT_FOUND)));
        }
        // imageUrls güncelleme ihtiyacı varsa servis içinde yönetebilirsin
        return mapper.toProductResponse(p);
    }

    @Override
    public void delete(UUID id) {
        var p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));
        productRepo.delete(p);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getBySlug(String slug) {
        var p = productRepo.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));
        return mapper.toProductResponse(p);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductListItemResponse> list(Pageable pageable) {
        return productRepo.findAllOrderByScore(pageable).map(mapper::toProductListItem);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductListItemResponse> listByCategory(UUID categoryId, Pageable pageable) {
        return productRepo.findByCategoryOrderByScore(categoryId, pageable)
                .map(mapper::toProductListItem);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductListItemResponse> search(String q, UUID categoryId, Pageable pageable) {
        return productRepo.search(q, categoryId, pageable).map(mapper::toProductListItem);
    }
}
