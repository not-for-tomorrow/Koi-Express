package com.koi_express.entity.audit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.PaymentMethod;
import com.koi_express.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id", "customer_id"})})
public class TransactionLogs { // Nhật ký giao dịch

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long transactionId;

    @Column(unique = true, nullable = false)
    String transactionCode;  // Mã giao dịch duy nhất

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @Positive(message = "Transaction amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Amount must be a valid monetary value")
    BigDecimal amount;  // Sử dụng BigDecimal cho giá trị tiền tệ

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    TransactionStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.transactionCode = "KE-" + UUID.randomUUID().toString();
    }
}
