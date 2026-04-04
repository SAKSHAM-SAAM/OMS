package com.oms.inventory_service.kafka.dto;

import java.util.*;

import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderCreatedEvent {
    private String customerId;
    private String orderReference;
    private List<OrderItemInfo> items;
}