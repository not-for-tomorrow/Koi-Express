package com.koi_express.entity;

import com.koi_express.enums.SupportRequestsStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long requestId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    String subject;
    String description;

    @Enumerated(EnumType.STRING)
    SupportRequestsStatus supportRequestsStatus;

    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime updatedAt = LocalDateTime.now();
}
