package com.koi_express.entity.shipment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"origin", "destination"})
})

public class Routes {//Quản lý tuyến đường

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long routeId;

    @NotEmpty(message = "Origin cannot be empty")
    String origin;

    @NotEmpty(message = "Destination cannot be empty")
    String destination;

    @Positive(message = "Distance must be positive")
    BigDecimal distanceKm;
    boolean optimal;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    LocalDateTime updatedAt = LocalDateTime.now();
}
