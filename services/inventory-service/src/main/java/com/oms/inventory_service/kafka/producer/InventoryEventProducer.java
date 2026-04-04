package com.oms.inventory_service.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC_RESERVED = "inventory.reserved";
    private static final String TOPIC_INSUFFICIENT = "inventory.insufficient";
    
    public void publishInventoryReserved(String orderReference){
        log.info("Inventory Service : Kafka producer invoked for {} for reserved status.", orderReference);
        kafkaTemplate.send(TOPIC_RESERVED, "ORDER_RESERVED:" + orderReference);
    }
    public void publishInventoryInsufficient(String orderReference, String sku){
        log.info("Inventory Service : Kafka Producer invoked , data insufficient case {} ", orderReference);
        kafkaTemplate.send(TOPIC_INSUFFICIENT, "ORDER_INSUFFICIENT: "+ orderReference + ":" + sku);
    }
}