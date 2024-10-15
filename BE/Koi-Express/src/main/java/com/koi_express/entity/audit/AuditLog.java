package com.koi_express.entity.audit;

import java.time.LocalDateTime;

import com.koi_express.entity.customer.Customers;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLog { // Ghi lại hành động của người dùng (khách hàng)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @Column(nullable = false)
    String action;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime timestamp;

    @Lob
    String details;

    String ipAddress;
}
