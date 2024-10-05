package com.koi_express.entity.order;

import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Orders { // Quản lý đơn hàng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @ManyToOne
    @JoinColumn(name ="delivering_staff_id", nullable = false)
    DeliveringStaff deliveringStaff;

    @NotEmpty(message = "Origin location cannot be empty")
    String originLocation;

    @NotEmpty(message = "Destination location cannot be empty")
    String destinationLocation;

    String originDetail;

    String destinationDetail;

    double totalFee;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    OrderDetail orderDetail;
}