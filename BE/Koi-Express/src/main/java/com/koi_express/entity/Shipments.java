package com.koi_express.entity;

import com.koi_express.enums.ShipmentStatus;
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
public class Shipments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long shipmentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;

    @ManyToOne
    @JoinColumn(name = "delivering_staff_id", nullable = false)
    Customers deliveringStaff;

    LocalDateTime pickupDate;
    LocalDateTime estimatedDeliveryDate;
    LocalDateTime actualDeliveryDate;

    boolean healthChecked;
    String packingMethod;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    Routes route;

    @Enumerated(EnumType.STRING)
    ShipmentStatus status;

    LocalDateTime updatedAt = LocalDateTime.now();
}
