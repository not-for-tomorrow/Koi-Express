package com.koi_express.entity;

import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

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

    @Pattern(regexp = "\\d{10}", message = "Phone number must contain exactly 10 digits")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    @Column(nullable = true, unique = true)
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

}
