package com.oms.inventory_service.kafka.dto;

import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderItemInfo {
    private String sku;
    private Integer quantity;
}