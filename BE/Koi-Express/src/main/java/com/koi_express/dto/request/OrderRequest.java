package com.koi_express.dto.request;

import java.math.BigDecimal;

import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "Sender name is required")
    String senderName;

    @NotEmpty(message = "Sender phone is required")
    String senderPhone;

    @NotEmpty(message = "Recipient name is required")
    String recipientName;

    @NotEmpty(message = "Recipient phone is required")
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
