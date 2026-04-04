package com.oms.inventory_service.exception;

public class InventoryItemNotFoundException extends RuntimeException{
    public InventoryItemNotFoundException(String message){
        super(message);
    }
}