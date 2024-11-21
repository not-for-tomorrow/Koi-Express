package com.koi_express.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportRequest {

    Long requestId;
    Long customerId;
    String subject;
    String description;
    String supportRequestsStatus;
    LocalDateTime createdAt;
}
