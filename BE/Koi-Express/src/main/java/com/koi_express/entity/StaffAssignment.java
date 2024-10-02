package com.koi_express.entity;

import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"staff_id", "order_id"})}
)
public class StaffAssignment { //Phân công nhân viên

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Customers staff;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
