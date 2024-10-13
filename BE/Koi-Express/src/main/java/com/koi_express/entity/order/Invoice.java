package com.koi_express.entity.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.InvoiceStatus; // Enum mới cho trạng thái hóa đơn
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
public class Invoice { // Thông tin hóa đơn thanh toán của khách hàng

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
    BigDecimal commitmentFee; // Dùng BigDecimal cho các giá trị tiền tệ

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal totalAmount;

    @Column(nullable = false)
    @CreationTimestamp
    LocalDateTime issuedAt; // Tự động gán thời gian phát hành hóa đơn

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    InvoiceStatus status; // Sử dụng enum cho trạng thái hóa đơn

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentMethod paymentMethod;
}
