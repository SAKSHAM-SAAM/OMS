package com.oms.inventory_service.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.oms.inventory_service.kafka.dto.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryEventConsumer {
    private final InventoryService inventoryService;
    @KafkaListener(topics = "order.created" , groupId = "Inventory-service-group")
    public void consume(OrderCreatedEvent event){
        log.info("Inventory Service : Kafka consuming event : {} ", event);
        inventoryService.processOrderCreated(event);
    }
}