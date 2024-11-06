package com.koi_express.dto.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentData {

    String transactionId;

    BigDecimal amount;

    String responseCode;

    String bankCode;

    String secureHash;

    String orderInfo;

    String transactionStatus;
}
