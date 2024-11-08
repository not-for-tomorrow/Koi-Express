package com.koi_express.entity.customer;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.FeedbackTag;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @JsonIgnore
    Orders order;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    Customers customer;

    @ManyToOne
    @JoinColumn(name = "delivering_staff_id", nullable = false)
    @JsonIgnore
    DeliveringStaff deliveringStaff;

    @ElementCollection(targetClass = FeedbackTag.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_feedback_tags", joinColumns = @JoinColumn(name = "feedback_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    Set<FeedbackTag> tags;

    @Column(nullable = true)
    String comments;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    int rating;

    @Column(nullable = false)
    LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }
}
