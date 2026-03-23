package com.oms.inventory_service.dto.response;

import com.oms.inventory_service.domain.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class StockReservationResponse {
    private Long id;
    private String sku;
    private String orderReference;
    private Integer quantityReserved;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}