package com.koi_express.entity;

import com.koi_express.enums.ShipmentCondition;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentLog { //ghi lại quá trình vận chuyển

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "shipment_id", nullable = false)
    Shipments shipment;

    @Column(nullable = false)
    LocalDateTime timestamp;

    @Column(nullable = false)
    String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_condition", nullable = false)
    ShipmentCondition shipmentCondition;

    String remarks;
}
