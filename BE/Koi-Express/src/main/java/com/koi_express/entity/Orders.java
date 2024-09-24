package com.koi_express.entity;

import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.PaymentMethod;
import com.koi_express.enums.TransportMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @NotEmpty(message = "Koi type cannot be empty")
    String koiType;

    @Positive(message = "Koi weight must be positive")
    double koiWeight;

    @Positive(message = "Koi quantity must be positive")
    int koiQuantity;

    @Positive(message = "Koi quantity must be positive")
    double koiSize;

    @Enumerated(EnumType.STRING)
    TransportMethod transportMethod;

    @NotEmpty(message = "Origin location cannot be empty")
    String originLocation;

    @NotEmpty(message = "Destination location cannot be empty")
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

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
