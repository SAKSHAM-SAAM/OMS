package com.oms.order_service.controller;

import com.oms.order_service.domain.enums.OrderEvent;
import com.oms.order_service.domain.enums.OrderStatus;
import com.oms.order_service.dto.request.CreateOrderRequest;
import com.oms.order_service.dto.response.OrderResponse;
import com.oms.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Order Service.
 *
 * All routes prefixed with /api/orders
 * Gateway routes traffic here from port 8080 → 8081
 */
@RestController
@RequestMapping("/api/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders — create a new order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders customerId={}", request.getCustomerId());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/orders/{reference} — get single order
    @GetMapping("/{orderReference}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable String orderReference) {
        return ResponseEntity.ok(orderService.getByReference(orderReference));
    }

    // GET /api/orders/customer/{customerId} — all orders for a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getByCustomer(
            @PathVariable String customerId) {
        return ResponseEntity.ok(orderService.getByCustomerId(customerId));
    }

    // GET /api/orders/status/{status} — filter by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getByStatus(
            @PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getByStatus(status));
    }

    // PATCH /api/orders/{reference}/event — apply state machine event
    @PatchMapping("/{orderReference}/event")
    public ResponseEntity<OrderResponse> applyEvent(
            @PathVariable String orderReference,
            @RequestParam OrderEvent event) {
        log.info("PATCH /api/orders/{}/event event={}", orderReference, event);
        return ResponseEntity.ok(orderService.applyEvent(orderReference, event));
    }
}