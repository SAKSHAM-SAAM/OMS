package com.oms.inventory_service.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class InventoryItemResponse {
    private Long id;
    private String sku;
    private String productName;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private String warehouseLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}