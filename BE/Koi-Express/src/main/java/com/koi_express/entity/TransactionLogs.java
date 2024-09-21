package com.koi_express.entity;

import com.koi_express.enums.PaymentMethod;
import com.koi_express.enums.TransactionStatus;
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
public class TransactionLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long transactionId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    double amount;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    TransactionStatus status;

    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();
}
