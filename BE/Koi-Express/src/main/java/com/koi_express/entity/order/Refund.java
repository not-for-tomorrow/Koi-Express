package com.koi_express.entity.order;

import com.koi_express.entity.customer.Customers;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Refund { // yêu cầu hoàn tiền của khách hàng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @Column(nullable = false)
    double refundAmount;

    String reason;

    @Column(nullable = false)
    LocalDateTime requestedAt;

    LocalDateTime processedAt;

    @Column(nullable = false)
    String status;
}
