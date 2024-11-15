package com.koi_express.dto.request;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerTopSpenderRequest {

    Long customerId;

    String fullName;

    BigDecimal totalSpent;

    Long orderCount;
}
