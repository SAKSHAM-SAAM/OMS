package com.oms.inventory_service.domain.repository;

import com.oms.inventory_service.domain.entity.StockReservation;
import com.oms.inventory_service.domain.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    StockReservation findByOrderReference(String orderReference);
    StockReservation findBySku(String sku);
    List<StockReservation> findByStatus(ReservationStatus status);
}