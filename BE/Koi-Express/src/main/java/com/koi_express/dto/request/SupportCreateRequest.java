package com.koi_express.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportCreateRequest {

    @NotEmpty(message = "Description is required")
    @Size(max = 5000, message = "Description cannot be longer than 5000 characters")
    String description;
}
