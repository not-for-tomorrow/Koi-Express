package com.koi_express.entity.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.PaymentMethod;
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
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal commitmentFee; // Phí cam kết

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal distanceFee; // Phí khoảng cách

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal careFee; // Phí chăm sóc

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal packagingFee; // Phí đóng gói

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal returnFee; // Phí trả hàng

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal vat; // Thuế VAT

    @Column(nullable = true, precision = 15, scale = 2)
    BigDecimal koiFee;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal insuranceFee; // Phí bảo hiểm

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal totalFee; // Tổng tiền hoá đơn

    @Column(nullable = false)
    @CreationTimestamp
    LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentMethod paymentMethod;
}
