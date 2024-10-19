package com.koi_express.entity.shipment;

import java.time.LocalDateTime;
import java.util.List;

import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.ShipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shipments { // quản lý vận chuyển

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long shipmentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    Orders order;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @ManyToOne
    @JoinColumn(name = "delivering_staff_id", nullable = false)
    DeliveringStaff  deliveringStaff;

    LocalDateTime pickupDate;
    LocalDateTime estimatedDeliveryDate;
    LocalDateTime actualDeliveryDate;

    @Column(nullable = false)
    boolean healthChecked = false;

    @NotEmpty(message = "Packing method cannot be empty")
    String packingMethod;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    Routes route;

    @Enumerated(EnumType.STRING)
    ShipmentStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    LocalDateTime updatedAt = LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "shipment_inspection_images", joinColumns = @JoinColumn(name = "shipment_id"))
    @Column(name = "inspection_image_url")
    List<String> inspectionImageUrls;

    // Transport images
    @ElementCollection
    @CollectionTable(name = "shipment_transport_images", joinColumns = @JoinColumn(name = "shipment_id"))
    @Column(name = "transport_image_url")
    List<String> transportImageUrls;

    // Delivery images
    @ElementCollection
    @CollectionTable(name = "shipment_delivery_images", joinColumns = @JoinColumn(name = "shipment_id"))
    @Column(name = "delivery_image_url")
    List<String> deliveryImageUrls;
}
