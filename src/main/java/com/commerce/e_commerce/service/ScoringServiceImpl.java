package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.ProductMetrics;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.repository.catalog.ProductMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService {

    private final ProductMetricsRepository metricsRepo;

    // skor parametreleri (config'den almak istersen @Value ile enjekte edebilir)
    private static final double MU   = 4.2;   // global rating ortalaması
    private static final double M    = 10.0;  // Bayesian prior ağırlığı
    private static final double WR   = 0.6;   // rating ağırlık
    private static final double WS   = 0.4;   // satış ağırlık
    private static final double SMAX = 500.0; // normalize tepe (30g satış)

    @Override
    @Transactional
    public void recomputeForProduct(UUID productId) {
        ProductMetrics pm = metricsRepo.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new ApiException("PRODUCT_METRICS_NOT_FOUND", HttpStatus.NOT_FOUND));
        compute(pm);
        metricsRepo.save(pm);
    }

    @Override
    @Transactional
    public void recomputeBulk(Collection<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) return;
        for (UUID pid : productIds) {
            metricsRepo.findByProductIdForUpdate(pid).ifPresent(pm -> {
                compute(pm);
                metricsRepo.save(pm);
            });
        }
    }

    private void compute(ProductMetrics pm) {
        double bayes = (MU * M + pm.getRatingAvg() * pm.getRatingCount()) / (M + pm.getRatingCount());
        double salesScore = Math.log10(1.0 + Math.max(0, pm.getSoldLast30d()));
        double normSales = salesScore / Math.log10(1.0 + SMAX);
        pm.setBestsellerScore(WR * (bayes / 5.0) + WS * normSales);
    }
}
