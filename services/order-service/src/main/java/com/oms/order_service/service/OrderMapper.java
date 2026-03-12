package com.oms.order_service.service;

import com.oms.order_service.domain.entity.Order;
import com.oms.order_service.domain.entity.OrderItem;
import com.oms.order_service.dto.request.CreateOrderRequest;
import com.oms.order_service.dto.request.OrderItemRequest;
import com.oms.order_service.dto.response.OrderItemResponse;
import com.oms.order_service.dto.response.OrderResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Converts between DTOs and Entities.
 * Keeps Service layer clean — no manual field mapping there.
 */
@Component
public class OrderMapper {

    public Order toEntity(CreateOrderRequest request) {
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .customerEmail(request.getCustomerEmail())
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingCountry(request.getShippingCountry())
                .totalAmount(calculateTotal(request.getItems()))
                .build();

        request.getItems().stream()
                .map(this::toItemEntity)
                .forEach(order::addItem);

        return order;
    }

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderReference(order.getOrderReference())
                .customerId(order.getCustomerId())
                .customerEmail(order.getCustomerEmail())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingCountry(order.getShippingCountry())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems().stream().map(this::toItemResponse).toList())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItem toItemEntity(OrderItemRequest request) {
        return OrderItem.builder()
                .sku(request.getSku())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        BigDecimal subtotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        return OrderItemResponse.builder()
                .id(item.getId())
                .sku(item.getSku())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(subtotal)
                .build();
    }

    private BigDecimal calculateTotal(List<OrderItemRequest> items) {
        return items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}