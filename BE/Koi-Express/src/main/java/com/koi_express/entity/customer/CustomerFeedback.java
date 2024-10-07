package com.koi_express.entity.customer;

import java.time.LocalDateTime;

import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.FeedbackType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerFeedback { // thu thập phản hồi và đánh gi từ khách hàng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Orders order;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @ManyToOne
    @JoinColumn(name = "delivering_staff_id", nullable = false)
    DeliveringStaff deliveringStaff;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    FeedbackType feedbackType;

    String comments;

    @Column(nullable = false)
    int rating; // 1 -> 5

    @Column(nullable = false)
    LocalDateTime submittedAt;
}
