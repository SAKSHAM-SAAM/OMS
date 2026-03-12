package com.oms.order_service.domain.enums;
/// All possible status 
public enum OrderStatus{
    PENDING,              // Order received, awaiting payment
    PAYMENT_CAPTURED,     // Payment confirmed
    INVENTORY_RESERVED,   // Stock locked for this order
    PROCESSING,           // Warehouse is picking & packing
    SHIPPED,              // Handed to carrier
    DELIVERED,            // Successfully delivered
    CANCELLED,            // Order cancelled
    REFUNDED              // Payment returned
};