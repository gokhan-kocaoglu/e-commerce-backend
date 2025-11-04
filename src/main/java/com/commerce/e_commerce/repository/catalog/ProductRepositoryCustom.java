package com.commerce.e_commerce.repository.catalog;

import com.commerce.e_commerce.domain.catalog.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductRepositoryCustom {
    Page<Product> search(String q, UUID categoryId, Pageable pageable);
}
