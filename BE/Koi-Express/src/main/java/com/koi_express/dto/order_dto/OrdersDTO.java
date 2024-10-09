package com.koi_express.dto.order_dto;

import java.time.LocalDateTime;

import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdersDTO {
    private Long orderId;
    private String originLocation;
    private String destinationLocation;
    private double totalFee;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}
