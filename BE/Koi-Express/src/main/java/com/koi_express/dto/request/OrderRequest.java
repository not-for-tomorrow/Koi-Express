package com.koi_express.dto.request;

import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.PackingMethod;
import com.koi_express.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {

    Customers customer;

    String recipientName;

    String recipientPhone;

    String koiType;

    int koiQuantity;

    String originLocation;

    String destinationLocation;

    PackingMethod packingMethod;

    PaymentMethod paymentMethod;

    boolean insurance = true;

    boolean specialCare = true;

    boolean healthCheck = true;
}
