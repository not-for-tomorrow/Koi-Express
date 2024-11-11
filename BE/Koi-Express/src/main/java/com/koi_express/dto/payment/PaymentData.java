package com.koi_express.dto.payment;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
