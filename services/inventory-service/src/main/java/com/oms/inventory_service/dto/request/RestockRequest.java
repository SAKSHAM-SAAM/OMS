package com.oms.inventory_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class RestockRequest {
    @Min(1)
    @NotNull(message = "quantity cannot be null")
    private Integer quantity;
}
