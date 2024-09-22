package com.koi_express.entity;

import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "authProvider"})
})
public class Customers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long customerId;

    @Column(nullable = true, unique = true)
    String email;

    String fullName;

    String address;

    @Column(nullable = true)
    String passwordHash;

    @Column(nullable = true, unique = true)
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must consist of exactly 10 digits")
    String phoneNumber;

    @Enumerated(EnumType.STRING)
    AuthProvider authProvider;

    String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role = Role.CUSTOMER;

    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

}
