package com.koi_express.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.entity.shipment.Shipments;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
public class Orders implements Serializable { // Quản lý đơn hàng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    Long orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId", nullable = false)
    @JsonIgnore
    Customers customer;

    @ManyToOne
    @JoinColumn(name = "delivering_staff_id", nullable = true)
    DeliveringStaff deliveringStaff;

    @NotEmpty(message = "Origin location cannot be empty")
    String originLocation;

    @NotEmpty(message = "Destination location cannot be empty")
    String destinationLocation;

    String originDetail;
    String destinationDetail;

    boolean isPriceCalculated = false; // Đánh dấu đã tính tiền
    boolean isImageUploaded = false; // Đánh dấu đã upload ảnh
    boolean isPaymentConfirmed = false; // Đánh dấu đã xác nhận thanh toán

    @Column(precision = 15, scale = 2)
    BigDecimal totalFee;

    @OneToOne(mappedBy = "order")
    Shipments shipment;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //        @JsonIgnore
    OrderDetail orderDetail;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void markPriceCalculated() {
        this.isPriceCalculated = true;
    }

    public void markImageUploaded() {
        this.isImageUploaded = true;
    }

    public void markPaymentConfirmed() {
        this.isPaymentConfirmed = true;
    }
}
