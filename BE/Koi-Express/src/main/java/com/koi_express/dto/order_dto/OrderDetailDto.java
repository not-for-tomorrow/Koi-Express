package com.koi_express.dto.order_dto;

import com.koi_express.enums.PackingMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailDto {
    private Long orderDetailId;
    private String senderName;
    private String recipientName;
    private double koiWeight;
    private boolean insurance;
    private PackingMethod packingMethod;
}
