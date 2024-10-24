package com.koi_express.entity.shipment;

import java.time.LocalDateTime;
import java.util.List;

import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.ShipmentCondition;
import com.koi_express.enums.ShipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
@Table(name = "shipments")
public class Shipments { // quản lý vận chuyển

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long shipmentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    Orders order;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @ManyToOne
    @JoinColumn(name = "delivering_staff_id", nullable = false)
    DeliveringStaff deliveringStaff;

    LocalDateTime estimatedPickupTime;

    LocalDateTime estimatedDeliveryTime;

    @Enumerated(EnumType.STRING)
    ShipmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_condition")
    ShipmentCondition condition;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
    
}
