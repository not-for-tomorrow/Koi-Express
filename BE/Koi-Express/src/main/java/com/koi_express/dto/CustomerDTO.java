package com.koi_express.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDTO {

    Long customerId;
    String fullName;
    String email;
    String phoneNumber;
    String address;
    String role;
    String authProvider;
}
