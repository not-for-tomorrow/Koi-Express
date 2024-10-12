package com.koi_express.dto.request;

import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.KoiType;
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

    String senderName;

    String senderPhone;

    String recipientName;

    String recipientPhone;

    int koiQuantity;

    String originLocation;

    String destinationLocation;

    String originDetail;

    String destinationDetail;

    PaymentMethod paymentMethod;

    boolean insuranceSelected;

    Double kilometers;
}
