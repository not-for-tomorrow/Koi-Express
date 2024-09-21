package com.koi_express.entity;

import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import jakarta.persistence.*;
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
    private Long customerId;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;

    private String address;

    @Column(nullable = true)
    private String passwordHash;

    @Column(nullable = true, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}
