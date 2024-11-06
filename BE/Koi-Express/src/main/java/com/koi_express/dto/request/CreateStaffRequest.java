package com.koi_express.dto.request;

import com.koi_express.enums.DeliveringStaffLevel;
import com.koi_express.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateStaffRequest {

    @NotEmpty(message = "Full name is required")
    String fullName;

    @Email(message = "Email should be valid")
    String email;

    @NotEmpty(message = "Password is required")
    String password;

    @NotEmpty(message = "Phone number is required")
    String phoneNumber;

    String address;

    DeliveringStaffLevel level;

    Role role;
}
