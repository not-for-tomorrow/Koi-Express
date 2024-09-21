package com.koi_express.entity;

import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.TransportMethod;
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
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    String koiType;
    double koiWeight;
    int koiQuantity;
    double koiSize;

    @Enumerated(EnumType.STRING)
    TransportMethod transportMethod;

    String originLocation;
    String destinationLocation;

    boolean insurance;
    boolean specialCare;
    boolean healthCheck;

    double distanceFee;
    double careFee;
    double tollFee;
    double weightFee;
    double totalFee;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime updatedAt = LocalDateTime.now();
}
