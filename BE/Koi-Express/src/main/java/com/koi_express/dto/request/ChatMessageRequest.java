package com.koi_express.dto.request;

import com.koi_express.entity.account.Staff;
import com.koi_express.entity.customer.Customers;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageRequest {

    Customers customer;

    Staff staff;

    String content;
}
