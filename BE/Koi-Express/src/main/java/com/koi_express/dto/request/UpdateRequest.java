package com.koi_express.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRequest {

    String email;
    String passwordHash;
    String phoneNumber;
    String fullName;
    String address;
}
