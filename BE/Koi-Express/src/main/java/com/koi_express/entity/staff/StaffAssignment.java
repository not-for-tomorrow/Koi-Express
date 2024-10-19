package com.koi_express.entity.staff;

import java.time.LocalDateTime;

import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"staff_id", "order_id"})})
public class StaffAssignment { // Phân công nhân viên

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    Long assignmentId;

    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "staff_id", nullable = false)
    DeliveringStaff staff;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    Orders order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
