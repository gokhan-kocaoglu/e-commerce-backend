package com.commerce.e_commerce.mapper;

import com.commerce.e_commerce.domain.inventory.Stock;
import com.commerce.e_commerce.domain.inventory.StockMovement;
import com.commerce.e_commerce.dto.inventory.StockMovementResponse;
import com.commerce.e_commerce.dto.inventory.StockResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapstructConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {

    default StockResponse toStockResponse(Stock s) {
        int onHand = s.getQuantityOnHand();
        int reserved = s.getQuantityReserved();
        int available = Math.max(0, onHand - reserved);
        String status = available == 0 ? "OUT_OF_STOCK"
                : available <= 5 ? "LOW"
                : "IN_STOCK";
        return new StockResponse(
                s.getVariant().getId(),
                onHand,
                reserved,
                available,
                status
        );
    }

    default StockMovementResponse toMovementResponse(StockMovement m) {
        return new StockMovementResponse(
                m.getId(),
                m.getVariant().getId(),
                m.getType(),
                m.getQuantity(),
                m.getReason(),
                m.getCreatedAt()
        );
    }
}