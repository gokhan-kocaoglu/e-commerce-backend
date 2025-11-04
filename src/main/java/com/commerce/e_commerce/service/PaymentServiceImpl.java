package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.common.enums.OrderStatus;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.repository.catalog.ProductMetricsRepository;
import com.commerce.e_commerce.repository.inventory.StockRepository;
import com.commerce.e_commerce.repository.order.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepo;
    private final StockRepository stockRepo;
    private final ProductMetricsRepository metricsRepo;
    private final ScoringService scoringService;

    @Override
    public void captureOrder(UUID orderId, String paymentRef) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ApiException("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new ApiException("ORDER_STATUS_INVALID", HttpStatus.BAD_REQUEST);
        }

        order.getItems().forEach(oi -> {
            var st = stockRepo.findByVariantId(oi.getVariant().getId())
                    .orElseThrow(() -> new ApiException("STOCK_NOT_TRACKED", HttpStatus.BAD_REQUEST));

            st.setQuantityReserved(st.getQuantityReserved() - oi.getQuantity());
            st.setQuantityOnHand(st.getQuantityOnHand() - oi.getQuantity());

            var pm = metricsRepo.findByProductIdForUpdate(oi.getVariant().getProduct().getId())
                    .orElseThrow(() -> new ApiException("PRODUCT_METRICS_NOT_FOUND", HttpStatus.INTERNAL_SERVER_ERROR));
            pm.setTotalSold(pm.getTotalSold() + oi.getQuantity());
            pm.setSoldLast30d(pm.getSoldLast30d() + oi.getQuantity());
            // sonra toplu save yapılacak
        });

        order.setStatus(OrderStatus.PAID);
        // metrics recompute (ürün bazında)
        order.getItems().forEach(oi ->
                scoringService.recomputeForProduct(oi.getVariant().getProduct().getId())
        );
    }
}
