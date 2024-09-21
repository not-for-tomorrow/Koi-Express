package com.koi_express.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Routes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long routeId;

    String origin;
    String destination;
    double distanceKm;
    boolean optimal;

    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();
}
