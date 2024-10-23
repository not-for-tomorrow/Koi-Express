package com.koi_express.dto;

import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.OrderDetail;
import com.koi_express.entity.order.Orders;
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
}
