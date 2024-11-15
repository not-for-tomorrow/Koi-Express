package com.koi_express.dto.request;

import java.math.BigDecimal;

import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
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

    @Builder.Default
    boolean insuranceSelected = false;

    @NotNull(message = "Distance (kilometers) is required")
    BigDecimal kilometers;
}
