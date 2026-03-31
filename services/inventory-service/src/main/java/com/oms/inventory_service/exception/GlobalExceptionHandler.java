package com.oms.inventory_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(InventoryItemNotFoundException ex){
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidState(InsufficientStockException ex){
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }
    // Handles @Valid Failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException except){
        Map<String, Object> fieldErrors = new HashMap<String, Object>();
        except.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            fieldErrors.put(field, error.getDefaultMessage());
        });
        Map<String, Object> body = new HashMap<>();
        body.put("Status" , "VALIDATION_FAILED");
        body.put("errors", fieldErrors);
        body.put("timeStamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.name());
        body.put("message", message);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}