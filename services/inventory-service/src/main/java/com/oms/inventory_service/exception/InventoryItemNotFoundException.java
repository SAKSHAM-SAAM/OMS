package com.oms.inventory_service.exception;

public class InventoryItemNotFoundException extends RuntimeException{
    InventoryItemNotFoundException(String message){
        super(message);
    }
}