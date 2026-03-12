package com.oms.order_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.order_service.domain.entity.Order;
import com.oms.order_service.domain.entity.OutboxEvent;
import com.oms.order_service.domain.enums.OrderEvent;
import com.oms.order_service.domain.enums.OrderStatus;
import com.oms.order_service.domain.repository.OrderRepository;
import com.oms.order_service.domain.repository.OutboxEventRepository;
import com.oms.order_service.dto.request.CreateOrderRequest;
import com.oms.order_service.dto.response.OrderResponse;
import com.oms.order_service.exception.InvalidOrderStateException;
import com.oms.order_service.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;
    private final StateMachine<OrderStatus, OrderEvent> stateMachine;

    /**
     * Creates order + saves outbox event in ONE transaction.
     * Either both succeed or both rollback — no lost events.
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customerId={}", request.getCustomerId());

        Order order = orderMapper.toEntity(request);
        Order saved = orderRepository.save(order);

        // Save outbox event atomically with the order
        saveOutboxEvent(saved, "order.created");

        log.info("Order created: ref={} total={}",
                saved.getOrderReference(), saved.getTotalAmount());

        return orderMapper.toResponse(saved);
    }

    public OrderResponse getByReference(String orderReference) {
        Order order = orderRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new OrderNotFoundException(orderReference));
        return orderMapper.toResponse(order);
    }

    public List<OrderResponse> getByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> getByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Applies a state machine event to transition order status.
     * State machine rejects illegal transitions automatically.
     */
    @Transactional
    public OrderResponse applyEvent(String orderReference, OrderEvent event) {
        Order order = orderRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new OrderNotFoundException(orderReference));

        log.info("Applying event={} to order={} currentStatus={}",
                event, orderReference, order.getStatus());

        // Reset and set current state on the state machine
        stateMachine.stopReactively().block();
        stateMachine.startReactively().block();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(accessor ->
                        accessor.resetStateMachineReactively(null).block()
                );

        // Send the event
        boolean accepted = Boolean.TRUE.equals(
                stateMachine.sendEvent(
                        Mono.just(MessageBuilder.withPayload(event).build())
                ).blockFirst()
        );

        if (!accepted) {
            throw new InvalidOrderStateException(
                    String.format("Event %s is not valid for order in state %s",
                            event, order.getStatus())
            );
        }

        // Update order status from state machine current state
        OrderStatus newStatus = stateMachine.getState().getId();
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);

        // Publish appropriate outbox event
        String topic = newStatus == OrderStatus.CANCELLED
                ? "order.cancelled" : "order.updated";
        saveOutboxEvent(updated, topic);

        log.info("Order {} transitioned to {}", orderReference, newStatus);
        return orderMapper.toResponse(updated);
    }

    private void saveOutboxEvent(Order order, String eventType) {
        try {
            String payload = objectMapper.writeValueAsString(orderMapper.toResponse(order));
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(order.getOrderReference())
                    .aggregateType("ORDER")
                    .eventType(eventType)
                    .payload(payload)
                    .build();
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to save outbox event for order={}", order.getOrderReference(), e);
            throw new RuntimeException("Failed to save outbox event", e);
        }
    }
}