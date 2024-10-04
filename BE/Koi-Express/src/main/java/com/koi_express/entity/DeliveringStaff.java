package com.koi_express.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveringStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long staffId;

    @Column(nullable = false)
    String fullName;

    @Column(nullable = false, unique = true)
    String phoneNumber;

    @Column(nullable = false)
    String address;

    @OneToMany(mappedBy = "deliveringStaff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Orders> ordersReceived;

    @Column(nullable = false)
    double averageRating;

    @OneToMany(mappedBy = "deliveringStaff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<CustomerFeedback> feedbacks;

    @Column(nullable = false)
    boolean active;

    @Column(nullable = false)
    boolean currentlyDelivering;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;
}
