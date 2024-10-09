package com.koi_express.dto.order_dto;

import java.time.LocalDateTime;

import com.koi_express.enums.ShipmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentsDTO {
    private Long shipmentId;
    private LocalDateTime pickupDate;
    private LocalDateTime actualDeliveryDate;
    private ShipmentStatus status;
}
