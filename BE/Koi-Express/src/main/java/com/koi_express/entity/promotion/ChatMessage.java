package com.koi_express.entity.promotion;

import java.time.LocalDateTime;

import com.koi_express.entity.account.Staff;
import com.koi_express.entity.customer.Customers;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "staffId", nullable = false)
    Staff staff;

    @ManyToOne
    @JoinColumn(name = "customer_name", referencedColumnName = "customerId", nullable = false)
    Customers customer;

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    LocalDateTime timestamp;
}
