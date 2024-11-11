package com.koi_express.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koi_express.enums.KoiType;
import com.koi_express.enums.PaymentMethod;
import com.koi_express.enums.ShipmentCondition;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
public class OrderDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderDetailId;

    @OneToOne
    @JoinColumn(name = "order_id")
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
    @Column(nullable = true)
    KoiType koiType;

    @Positive(message = "Koi quantity must be positive")
    int koiQuantity;

    @PositiveOrZero(message = "Koi size must be positive or zero")
    @Digits(integer = 5, fraction = 2, message = "Koi size must be a valid number with up to 2 decimal places")
    BigDecimal koiSize;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    ShipmentCondition shipmentCondition;

    boolean insurance;

    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2, message = "Distance fee must be a valid monetary amount")
    BigDecimal distanceFee;

    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2, message = "Care fee must be a valid monetary amount")
    BigDecimal careFee;

    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2, message = "Packing fee must be a valid monetary amount")
    BigDecimal packagingFee;

    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2, message = "Return fee must be a valid monetary amount")
    BigDecimal returnFee;

    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2, message = "VAT must be a valid monetary amount")
    BigDecimal vat;

    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2, message = "Koi weight fee must be a valid monetary amount")
    BigDecimal koiFee;

    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2, message = "Insurance fee must be a valid monetary amount")
    BigDecimal insuranceFee;

    @PositiveOrZero(message = "Kilometers must be positive or zero")
    BigDecimal kilometers;

    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2, message = "Commitment fee must be a valid monetary amount")
    BigDecimal commitmentFee;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
