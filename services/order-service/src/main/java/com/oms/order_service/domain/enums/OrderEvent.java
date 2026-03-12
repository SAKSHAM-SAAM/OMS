package com.oms.order_service.domain.enums;

public enum OrderEvent {
    CAPTURE_PAYMENT,
    RESERVE_INVENTORY,
    START_PROCESSING,
    SHIP_ORDER,
    CONFIRM_DELIVERY,
    CANCEL_ORDER,
    REFUND_ORDER
}