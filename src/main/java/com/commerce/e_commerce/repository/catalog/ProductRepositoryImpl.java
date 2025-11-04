package com.commerce.e_commerce.repository.catalog;

import com.commerce.e_commerce.domain.catalog.Product;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
class ProductRepositoryImpl implements ProductRepositoryCustom {
    private final EntityManager em;

    @Override
    public Page<Product> search(String q, UUID categoryId, Pageable pageable) {
        var sb = new StringBuilder("""
            select p from Product p
            join ProductMetrics m on m.product=p
            where p.deleted=false
        """);
        if (q != null && !q.isBlank()) sb.append(" and lower(p.title) like :q ");
        if (categoryId != null) sb.append(" and p.category.id=:cid ");
        sb.append(" order by m.bestsellerScore desc, p.createdAt desc ");

        var query = em.createQuery(sb.toString(), Product.class);
        if (q != null && !q.isBlank()) query.setParameter("q", "%"+q.toLowerCase()+"%");
        if (categoryId != null) query.setParameter("cid", categoryId);

        var total = query.getResultList().size(); // prod'da count query yaz
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        var content = query.getResultList();
        return new PageImpl<>(content, pageable, total);
    }
}
