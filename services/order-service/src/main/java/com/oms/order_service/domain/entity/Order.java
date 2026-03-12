package com.oms.order_service.domain.entity;

import com.oms.order_service.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core Order entity — persisted to orders_db.
 * Owns the list of OrderItems (cascade all).
 */
@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Human-readable order reference e.g. ORD-550e8400
    @Column(nullable = false, unique = true)
    private String orderReference;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String customerEmail;

    // Shipping address fields
    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String shippingCity;

    @Column(nullable = false)
    private String shippingCountry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // One order has many items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Auto-generate order reference before first save
    @PrePersist
    public void prePersist() {
        if (this.orderReference == null) {
            this.orderReference = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }

    // Helper to add items and keep relationship consistent
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}