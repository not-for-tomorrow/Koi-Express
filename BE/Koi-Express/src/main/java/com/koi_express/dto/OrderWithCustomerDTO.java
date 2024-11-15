package com.koi_express.dto;

import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.Shipments;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderWithCustomerDTO {

    Orders order;

    Customers customer;

    Shipments shipments;
}
