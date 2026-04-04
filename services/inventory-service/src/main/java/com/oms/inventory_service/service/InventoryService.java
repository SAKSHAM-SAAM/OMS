package com.oms.inventory_service.service;

import org.springframework.stereotype.Service;

import com.oms.inventory_service.domain.entity.InventoryItem;
import com.oms.inventory_service.domain.entity.StockReservation;
import com.oms.inventory_service.domain.enums.ReservationStatus;
import com.oms.inventory_service.domain.repository.*;
import com.oms.inventory_service.dto.request.AddStockRequest;
import com.oms.inventory_service.dto.request.RestockRequest;
import com.oms.inventory_service.exception.DuplicateSkuException;
import com.oms.inventory_service.exception.GlobalExceptionHandler;
import com.oms.inventory_service.exception.InventoryItemNotFoundException;
import com.oms.inventory_service.kafka.dto.*;
import com.oms.inventory_service.kafka.producer.InventoryEventProducer;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

import javax.naming.InsufficientResourcesException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class InventoryService {
    private final InventoryItemRepository inventoryItemRepo;

    private final StockReservationRepository stockReservationRepo;
    
    private final InventoryEventProducer inventoryEventProducer;

    public void processOrderCreated(OrderCreatedEvent event) {

        // Check if OrderSKU exists in Repo, if yes,
        // Fetch the corresponding Inventory Item
        List<OrderItemInfo> items = event.getItems();
        Boolean isOrderPossible = true;
        String notAvailableItemSku = "";

        // 1 Loop over all the items in the event
        // Check if order fulfillment is possible
        for(OrderItemInfo item : items){
            String currentItemSku = item.getSku();

            // 1.A Fetch the item from inventory
            InventoryItem inventoryItem = inventoryItemRepo.findBySku(currentItemSku)
            .orElseThrow(() -> new InventoryItemNotFoundException("SKU not found "+ currentItemSku));

            // 1.B Check if quantity is enough for the satsfaction of current order
            if(inventoryItem.getAvailableQuantity() < item.getQuantity()){
                notAvailableItemSku = currentItemSku;
                isOrderPossible = false;
                break;
            }
        }

        // 2 If order fulfillment is possible , update all the DB entries 
        // for the same
        if(isOrderPossible){
            for(OrderItemInfo item : items){
                String currentItemSku = item.getSku();
                InventoryItem inventoryItem = inventoryItemRepo.findBySku(currentItemSku)
                    .orElseThrow(() -> new InventoryItemNotFoundException("SKU not found "+ currentItemSku));
                
                Integer newAvailability = inventoryItem.getAvailableQuantity() - item.getQuantity();
                //1.B.a Update available and reserved stock quantities.
                inventoryItem.setAvailableQuantity(newAvailability);
                //1.B.b Update Reserved Quantity of inventory
                inventoryItem.setReservedQuantity(item.getQuantity() + inventoryItem.getReservedQuantity());
                //1.B.c Update DB with changes made
                inventoryItemRepo.save(inventoryItem);

                StockReservation stock = StockReservation.builder().
                orderReference(event.getOrderReference()).sku(currentItemSku).
                quantityReserved(item.getQuantity()).status(ReservationStatus.RESERVED).build();
                stockReservationRepo.save(stock);
            }
        }
        // 2 Use flag to sort the handling of the event
        if(!isOrderPossible){
            inventoryEventProducer.publishInventoryInsufficient(event.getOrderReference(), notAvailableItemSku);
        }else{
            inventoryEventProducer.publishInventoryReserved(event.getOrderReference());
        }
    }

    public InventoryItem addStock(AddStockRequest request){
        // Check if the sku is already registered
        inventoryItemRepo.findBySku(request.getSku())
            .ifPresent(existing -> {throw new DuplicateSkuException("Sku already exists. "
            + request.getSku()); } );

        // Create new entry for stock
        InventoryItem newInventoryItem = InventoryItem.builder().sku(request.getSku())
            .productName(request.getProductName())
            .availableQuantity(request.getAvailableQuantity())
            .reservedQuantity(0)
            .warehouseLocation(request.getWarehouseLocation())
            .build();
        return inventoryItemRepo.save(newInventoryItem);
    }

    public InventoryItem restock(String sku, RestockRequest request){
        InventoryItem restockInventory = inventoryItemRepo.findBySku(sku).orElseThrow(() -> 
        new InventoryItemNotFoundException("Item does not exist " + sku));
        restockInventory.setAvailableQuantity(
            restockInventory.getAvailableQuantity() + request.getQuantity());
        return inventoryItemRepo.save(restockInventory);
    }

    public InventoryItem getInventoryItem(String sku){
        return inventoryItemRepo.findBySku(sku).orElseThrow(() -> 
        new InventoryItemNotFoundException("Item does not exist " + sku));

    }

    public List<InventoryItem> getAllInventoryItems(){
        return inventoryItemRepo.findAll();
    }

    public List<InventoryItem> getItemsByWarehouseLocation(String location){
        return inventoryItemRepo.findByWarehouseLocation(location);
    }
}