package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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

    // ---------- CREATE ----------
    @Override
    public ProductResponse create(ProductCreateRequest req) {
        // slug benzersizliği (soft-delete hariç)
        if (req.slug() != null && productRepo.existsBySlugAndDeletedFalse(req.slug())) {
            throw new ApiException("PRODUCT_SLUG_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
        }

        Product p = mapper.toProduct(req);

        // kategori resolve
        Category cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new ApiException("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND));
        p.setCategory(cat);

        // marka resolve (opsiyonel)
        if (req.brandId() != null) {
            Brand b = brandRepo.findById(req.brandId())
                    .orElseThrow(() -> new ApiException("BRAND_NOT_FOUND", HttpStatus.NOT_FOUND));
            p.setBrand(b);
        }

        // mapper.afterProductCreate image’ları set etti; ek kontrol (sort normalize)
        normalizeImageSortOrders(p);

        productRepo.save(p);

        // İlk metrikleri oluştur (0 değerlerle)
        ProductMetrics pm = new ProductMetrics();
        pm.setProduct(p);
        metricsRepo.save(pm);

        return mapper.toProductResponse(p, pm);
    }

    // ---------- UPDATE ----------
    @Override
    public ProductResponse update(UUID id, ProductUpdateRequest req) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));

        // slug değişiyorsa benzersizlik kontrolü
        if (req.slug() != null && !req.slug().equals(p.getSlug())
                && productRepo.existsBySlugAndDeletedFalse(req.slug())) {
            throw new ApiException("PRODUCT_SLUG_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
        }

        // temel alanları map et (BigDecimal fiyatlar mapper içinde cents’e çevrilecek)
        mapper.updateProduct(p, req);

        // kategori / marka resolve (opsiyonel patch)
        if (req.categoryId() != null) {
            Category cat = categoryRepo.findById(req.categoryId())
                    .orElseThrow(() -> new ApiException("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND));
            p.setCategory(cat);
        }
        if (req.brandId() != null) {
            Brand b = brandRepo.findById(req.brandId())
                    .orElseThrow(() -> new ApiException("BRAND_NOT_FOUND", HttpStatus.NOT_FOUND));
            p.setBrand(b);
        }

        // görseller replace (isteğe bağlı)
        if (req.imageUrls() != null) {
            p.getImages().clear();
            int i = 0;
            for (String url : req.imageUrls()) {
                if (url == null || url.isBlank()) continue;
                ProductImage img = new ProductImage();
                img.setProduct(p);
                img.setUrl(url);
                img.setSortOrder(i++);
                p.getImages().add(img);
            }
        }
        normalizeImageSortOrders(p);

        // mevcut metrikleri çek (varsa)
        ProductMetrics pm = metricsRepo.findByProductId(p.getId()).orElse(null);

        return mapper.toProductResponse(p, pm);
    }

    // ---------- DELETE ----------
    @Override
    public void delete(UUID id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));
        productRepo.delete(p); // SoftDelete devrede
    }

    // ---------- READ ----------
    @Transactional(readOnly = true)
    @Override
    public ProductResponse getBySlug(String slug) {
        Product p = productRepo.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));
        ProductMetrics pm = metricsRepo.findByProductId(p.getId()).orElse(null);
        return mapper.toProductResponse(p, pm);
    }



    @Transactional(readOnly = true)
    @Override
    public Page<ProductListItemResponse> list(Pageable pageable) {
        return productRepo.findAllOrderByScore(pageable)
                .map(mapper::toProductListItem);
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
        return productRepo.search(q, categoryId, pageable)
                .map(mapper::toProductListItem);
    }

    @Transactional(readOnly = true)
    @Override
    public java.util.List<ProductListItemResponse> topBestsellers(int limit) {
        int lim = Math.max(1, Math.min(limit, 50)); // güvenli sınır: 1..50
        var page = metricsRepo.findTopByBestseller(PageRequest.of(0, lim));
        return page.getContent().stream()
                .map(pm -> mapper.toProductListItem(pm.getProduct(), pm))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProduct(UUID id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));
        ProductMetrics pm = metricsRepo.findByProductId(p.getId()).orElse(null);
        return mapper.toProductResponse(p, pm);
    }

    // ---------- helpers ----------
    private void normalizeImageSortOrders(Product p) {
        if (p.getImages() == null || p.getImages().isEmpty()) return;
        p.getImages().sort(Comparator.comparingInt(ProductImage::getSortOrder));
        int idx = 0;
        for (ProductImage img : p.getImages()) {
            img.setSortOrder(idx++);
            img.setProduct(p);
        }
    }
}
