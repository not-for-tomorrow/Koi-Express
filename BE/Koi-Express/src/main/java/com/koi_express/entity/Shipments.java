package com.koi_express.entity;

import com.koi_express.enums.ShipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shipments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long shipmentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    Orders order;

    @ManyToOne
    @JoinColumn(name = "delivering_staff_id", nullable = false)
    Customers deliveringStaff;

    LocalDateTime pickupDate;
    LocalDateTime estimatedDeliveryDate;
    LocalDateTime actualDeliveryDate;

    @Column(nullable = false)
    boolean healthChecked = false;

    @NotEmpty(message = "Packing method cannot be empty")
    String packingMethod;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    Routes route;

    @Enumerated(EnumType.STRING)
    ShipmentStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();


    @UpdateTimestamp
    LocalDateTime updatedAt = LocalDateTime.now();
}
