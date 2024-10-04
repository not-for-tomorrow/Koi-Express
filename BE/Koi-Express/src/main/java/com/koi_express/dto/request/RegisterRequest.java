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
public class RegisterRequest {

    String fullName;

    @NotEmpty(message = "Phone number cannot be empty")
    String phoneNumber;

    @NotEmpty(message = "Password is required")
    String password;

    String email;

    String otp;
}
