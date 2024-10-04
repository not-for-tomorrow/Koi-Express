package com.koi_express.entity;

import com.koi_express.enums.FeedbackType;
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
public class CustomerFeedback { //thu thập phản hồi và đánh gi từ khách hàng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    FeedbackType feedbackType;

    String comments;

    @Column(nullable = false)
    int rating; // 1 -> 5

    @Column(nullable = false)
    LocalDateTime submittedAt;
}
