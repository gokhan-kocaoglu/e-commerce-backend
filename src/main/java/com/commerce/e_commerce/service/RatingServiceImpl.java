package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.ProductMetrics;
import com.commerce.e_commerce.domain.catalog.ProductRating;
import com.commerce.e_commerce.dto.catalog.RatingRequest;
import com.commerce.e_commerce.dto.catalog.RatingResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.CatalogMapper;
import com.commerce.e_commerce.repository.catalog.ProductMetricsRepository;
import com.commerce.e_commerce.repository.catalog.ProductRatingRepository;
import com.commerce.e_commerce.repository.catalog.ProductRepository;
import com.commerce.e_commerce.repository.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class RatingServiceImpl implements RatingService {

    private final ProductRatingRepository ratingRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final ProductMetricsRepository metricsRepo;
    private final CatalogMapper mapper;

    @Override
    public RatingResponse upsert(UUID userId, RatingRequest req) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));
        var product = productRepo.findById(req.productId())
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));

        var rating = ratingRepo.findByProductIdAndUserId(product.getId(), user.getId())
                .orElseGet(ProductRating::new);

        rating.setProduct(product);
        rating.setUser(user);
        rating.setRating(req.rating());
        rating.setComment(req.comment());
        ratingRepo.save(rating);

        // metrics (avg, count, score)
        var pm = metricsRepo.findByProductIdForUpdate(product.getId())
                .orElseThrow(() -> new ApiException("PRODUCT_METRICS_MISSING", HttpStatus.INTERNAL_SERVER_ERROR));

        pm.setRatingCount(ratingRepo.countByProduct(product.getId()));
        pm.setRatingAvg(ratingRepo.avgByProduct(product.getId()));

        recomputeScore(pm); // rating değişince skor güncelle
        metricsRepo.save(pm);

        return mapper.toRatingResponse(rating);
    }

    /* basit skor formülü */
    private void recomputeScore(ProductMetrics pm) {
        double MU = 4.2, M = 10.0, WR = 0.6, WS = 0.4, SMAX = 500.0;
        double bayes = (MU*M + pm.getRatingAvg()*pm.getRatingCount()) / (M + pm.getRatingCount());
        double sales = Math.log10(1 + Math.max(0, pm.getSoldLast30d()));
        double norm = sales / Math.log10(1 + SMAX);
        pm.setBestsellerScore(WR * (bayes/5.0) + WS * norm);
    }
}
