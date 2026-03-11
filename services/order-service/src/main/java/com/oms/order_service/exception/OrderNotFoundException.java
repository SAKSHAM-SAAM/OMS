package main.java.com.oms.order_service.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderReference) {
        super("Order not found: " + orderReference);
    }
}