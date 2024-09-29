package com.koi_express.entity;

import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.PackingMethod;
import com.koi_express.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
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
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @NotEmpty(message = "Recipient name cannot be empty")
    String recipientName;

    @NotEmpty(message = "Recipient phone cannot be empty")
    String recipientPhone;

    @NotEmpty(message = "Koi type cannot be empty")
    String koiType;


    double koiWeight;

    @Positive(message = "Koi quantity must be positive")
    int koiQuantity;


    double koiSize;


    @NotEmpty(message = "Origin location cannot be empty")
    String originLocation;

    @NotEmpty(message = "Destination location cannot be empty")
    String destinationLocation;

    boolean insurance;
    boolean specialCare;
    boolean healthCheck;

    @Enumerated(EnumType.STRING)
    PackingMethod packingMethod;

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
