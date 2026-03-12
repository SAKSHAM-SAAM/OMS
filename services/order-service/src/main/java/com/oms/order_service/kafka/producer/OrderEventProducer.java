package com.oms.order_service.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oms.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Publishes order domain events to Kafka topics.
 *
 * Uses KafkaTemplate which handles:
 * - Serialization (Order → JSON)
 * - Retries (configured in application.yml)
 * - Idempotent produces (no duplicate messages)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.order-created:order.created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.order-updated:order.updated}")
    private String orderUpdatedTopic;

    @Value("${kafka.topics.order-cancelled:order.cancelled}")
    private String orderCancelledTopic;

    public void publishOrderCreated(Order order) {
        publish(orderCreatedTopic, order.getOrderReference(), order);
    }

    public void publishOrderUpdated(Order order) {
        publish(orderUpdatedTopic, order.getOrderReference(), order);
    }

    public void publishOrderCancelled(Order order) {
        publish(orderCancelledTopic, order.getOrderReference(), order);
    }

    private void publish(String topic, String key, Object payload) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish to topic={} key={} error={}",
                        topic, key, ex.getMessage());
            } else {
                log.info("Published to topic={} key={} partition={} offset={}",
                        topic, key,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}