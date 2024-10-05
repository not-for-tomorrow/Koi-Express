package com.koi_express.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Promotion { // chương trình khuyến mãi

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String promoCode;

    String description;

    double discountAmount;

    int discountPercentage;

    @Column(nullable = false)
    LocalDateTime validFrom;

    @Column(nullable = false)
    LocalDateTime validTo;

    @Column(nullable = false)
    boolean active;
}