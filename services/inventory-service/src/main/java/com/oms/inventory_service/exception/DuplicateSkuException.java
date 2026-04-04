package com.oms.inventory_service.exception;

public class DuplicateSkuException extends RuntimeException{
    public DuplicateSkuException(String message){
        super(message);
    }
}