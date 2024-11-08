package com.koi_express.dto.request;

import com.koi_express.enums.FeedbackTag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackRequest {

    @Min(1)
    @Max(5)
    int rating;

    Set<FeedbackTag> tags;

    @Size(max = 500)
    String comments;

    @NotNull
    Long customerId;

    @NotNull
    Long orderId;
}
