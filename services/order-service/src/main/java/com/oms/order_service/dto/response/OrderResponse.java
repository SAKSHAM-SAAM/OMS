package com.oms.order_service.dto.response;

import com.oms.order_service.domain.enums.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderReference;
    private String customerId;
    private String customerEmail;
    private String shippingAddress;
    private String shippingCity;
    private String shippingCountry;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}