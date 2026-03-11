package main.java.com.oms.order_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Outbox Pattern — guarantees events are never lost.
 *
 * Flow:
 *  1. Order saved + OutboxEvent saved in SAME DB transaction
 *  2. Separate scheduler reads unpublished outbox events
 *  3. Publishes to Kafka, marks as PUBLISHED
 *
 * If app crashes after step 1, event is replayed on restart.
 * If Kafka is down, events queue up and publish when it recovers.
 */
@Entity
@Table(name = "outbox_events")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aggregateId;    // orderId

    @Column(nullable = false)
    private String aggregateType;  // "ORDER"

    @Column(nullable = false)
    private String eventType;      // "order.created"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;        // JSON of the event

    @Column(nullable = false)
    @Builder.Default
    private Boolean published = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;
}