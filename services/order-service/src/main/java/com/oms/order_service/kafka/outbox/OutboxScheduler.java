package com.oms.order_service.kafka.outbox;

import com.oms.order_service.domain.entity.OutboxEvent;
import com.oms.order_service.domain.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Outbox Scheduler — runs every 5 seconds.
 *
 * Reads unpublished OutboxEvents from DB and publishes to Kafka.
 * Marks them published so they aren't sent twice.
 *
 * This guarantees at-least-once delivery even if:
 * - App crashed after DB write but before Kafka publish
 * - Kafka was temporarily down
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)  // every 5 seconds
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> unpublished =
                outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc();

        if (unpublished.isEmpty()) return;

        log.debug("Processing {} outbox events", unpublished.size());

        for (OutboxEvent event : unpublished) {
            try {
                kafkaTemplate.send(
                        event.getEventType(),
                        event.getAggregateId(),
                        event.getPayload()
                );
                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());
                outboxEventRepository.save(event);

                log.info("Outbox event published: type={} aggregateId={}",
                        event.getEventType(), event.getAggregateId());

            } catch (Exception e) {
                log.error("Failed to publish outbox event id={} error={}",
                        event.getId(), e.getMessage());
                // Don't mark published — will retry next cycle
            }
        }
    }
}