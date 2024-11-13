package com.koi_express.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

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