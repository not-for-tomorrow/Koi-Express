package com.koi_express.entity.account;

import java.time.LocalDateTime;

import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.VerificationStatus;
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
public class UserVerification { // xác minh người dùng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @Column(nullable = false)
    String verificationCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    VerificationStatus status;

    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime expiresAt;

    LocalDateTime verifiedAt;
}
