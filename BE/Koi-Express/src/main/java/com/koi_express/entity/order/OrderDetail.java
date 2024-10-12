package com.koi_express.entity.order;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koi_express.enums.KoiType;
import com.koi_express.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderDetailId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    Orders order;

    @NotEmpty(message = "Sender name cannot be empty")
    String senderName;

    @NotEmpty(message = "Sender phone cannot be empty")
    String senderPhone;

    @NotEmpty(message = "Recipient name cannot be empty")
    String recipientName;

    @NotEmpty(message = "Recipient phone cannot be empty")
    String recipientPhone;

    @Enumerated(EnumType.STRING)
    KoiType koiType;

    @Positive(message = "Koi quantity must be positive")
    int koiQuantity;

    @PositiveOrZero(message = "Koi weight must be positive or zero")
    double koiWeight;

    @PositiveOrZero(message = "Koi size must be positive or zero")
    double koiSize;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    boolean insurance;
    boolean healthCheck;

    double distanceFee;
    double careFee;
    double tollFee;
    double weightFee;
    double packingFee;
    double storageFee;
    double returnFee;
    double vat;

    double fuelFee;
    double insuranceFee;

    @PositiveOrZero(message = "Kilometers must be positive or zero")
    double kilometers;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
