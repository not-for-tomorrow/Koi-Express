package com.koi_express.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Size(min = 10, max = 10, message = "Phone number must consist of exactly 10 digits")
    @Pattern(regexp = "\\d{10}", message = "Phone number must contain only digits")
    String phoneNumber;

    String fullName;
    String address;
}
