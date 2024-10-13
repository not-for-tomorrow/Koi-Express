package com.koi_express.entity.audit;

import java.time.LocalDateTime;

import com.koi_express.entity.customer.Customers;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLog { // Ghi lại hành động của người dùng (khách hàng)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Thay userId thành mối quan hệ với thực thể Customers
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @Column(nullable = false)
    String action; // Hành động của người dùng

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime timestamp; // Thời gian thực hiện hành động

    @Lob // Cho phép lưu trữ chuỗi lớn
    String details; // Thông tin chi tiết về hành động

    String ipAddress; // Địa chỉ IP từ người dùng thực hiện hành động
}
