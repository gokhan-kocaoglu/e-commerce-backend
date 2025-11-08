package com.commerce.e_commerce.repository.catalog;

import com.commerce.e_commerce.domain.catalog.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, ProductRepositoryCustom {

    boolean existsBySlugAndDeletedFalse(String slug);
    Optional<Product> findBySlugAndDeletedFalse(String slug);

    @Query("""
      select p from Product p
      join ProductMetrics m on m.product = p
      where p.deleted=false
      order by m.bestsellerScore desc, p.createdAt desc
    """)
    Page<Product> findAllOrderByScore(Pageable pageable);

    @Query("""
      select p from Product p
      join ProductMetrics m on m.product = p
      where p.deleted=false and p.category.id=:categoryId
      order by m.bestsellerScore desc, p.createdAt desc
    """)
    Page<Product> findByCategoryOrderByScore(@Param("categoryId") UUID categoryId, Pageable pageable);
}

