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
public class LoyaltyProgram { // chương trình khách hàng thân thiết

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @Column(nullable = false)
    int loyaltyPoints;

    @Column(nullable = false)
    LocalDateTime lastUpdated;

    @Column(nullable = false)
    String level;
}
