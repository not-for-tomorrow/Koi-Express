package com.koi_express.entity.shipment;

import java.time.LocalDateTime;
import java.util.List;

import com.koi_express.entity.customer.CustomerFeedback;
import com.koi_express.entity.customer.User;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveringStaff implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long staffId;

    @Column(nullable = false)
    String fullName;

    @Column(nullable = false, unique = true)
    String phoneNumber;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String address;

    @Column(nullable = false)
    String passwordHash;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @Override
    public Long getId() {
        return staffId;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public Role getRole() {
        return role;
    }
}
