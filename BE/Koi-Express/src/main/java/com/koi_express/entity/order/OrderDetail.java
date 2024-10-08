package com.koi_express.entity.order;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koi_express.enums.PackingMethod;
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

    @NotEmpty(message = "Koi type cannot be empty")
    String koiType;

    @Positive(message = "Koi quantity must be positive")
    int koiQuantity;

    @PositiveOrZero(message = "Koi weight must be positive or zero")
    double koiWeight;

    @PositiveOrZero(message = "Koi size must be positive or zero")
    double koiSize;

    @Enumerated(EnumType.STRING)
    PackingMethod packingMethod;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    boolean insurance;
    boolean specialCare;
    boolean healthCheck;

    double distanceFee;
    double careFee;
    double tollFee;
    double weightFee;

    @PositiveOrZero(message = "Kilometers must be positive or zero")
    double kilometers;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
