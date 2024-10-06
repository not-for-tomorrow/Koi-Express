package com.koi_express.entity.customer;

import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "authProvider"})
})
public class Customers implements User { // quản lí thông tin khách hàng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long customerId;

    @Column(nullable = true, unique = true)
    String email;

    String fullName;

    String address;

    @Column(nullable = true)
    String passwordHash;

    String phoneNumber;

    @Enumerated(EnumType.STRING)
    AuthProvider authProvider;

    String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role = Role.CUSTOMER;

    @Column(updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt = LocalDateTime.now();

    boolean activated;

    @Override
    public Long getId() {
        return customerId;
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
