package com.oms.inventory_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter @Getter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class AddStockRequest {
    @NotBlank(message = "Sku is required")
    private String sku;

    @NotBlank(message = "Product name is required, it cannot be empty")
    private String productName;

    @Min(1)
    @NotNull(message = "available Quantity is required")
    private Integer availableQuantity;

    @NotBlank(message = "warehouse Quantity is required")
    private String warehouseLocation;
}