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
    @Size(min = 10, max = 10, message = "Phone number must consist of exactly 10 digits")
    @Pattern(regexp = "\\d{10}", message = "Phone number must contain exactly 10 digits and only digits")
    String phoneNumber;

    @NotEmpty(message = "Password is required")
    String password;

    String email;
}
