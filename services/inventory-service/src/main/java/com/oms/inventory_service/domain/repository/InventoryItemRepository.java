package com.oms.inventory_service.domain.repository;

import com.oms.inventory_service.domain.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    InventoryItem findBySku(String sku);

    List<InventoryItem> findByWarehouseLocation(String location);
}