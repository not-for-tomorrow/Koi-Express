package com.koi_express.entity.image;

import com.koi_express.entity.shipment.Shipments;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "shipment_inspection_images")
public class ShipmentInspectionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "shipment_id", nullable = false)
    Shipments shipment;

    @Column(name = "inspection_image_url", nullable = false)
    String inspectionImageUrl;
}
