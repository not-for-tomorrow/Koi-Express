package com.koi_express.entity.customer;

import java.time.LocalDateTime;

import com.koi_express.enums.SupportRequestsStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"customer_id", "subject"})})
public class SupportRequests { // Yêu cầu hỗ trợ khách hàng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long requestId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @NotEmpty(message = "Subject cannot be empty")
    @Size(max = 255, message = "Subject cannot be longer than 255 characters")
    String subject;

    @NotEmpty(message = "Description cannot be empty")
    @Size(max = 5000, message = "Description cannot be longer than 5000 characters")
    String description;

    @Enumerated(EnumType.STRING)
    SupportRequestsStatus supportRequestsStatus;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    LocalDateTime updatedAt = LocalDateTime.now();

    LocalDateTime resolvedAt;
}
