package com.koi_express.entity.customer;

import java.time.LocalDateTime;

import com.koi_express.enums.LoyaltyLevel;
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
public class LoyaltyProgram { // Chương trình khách hàng thân thiết

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customers customer;

    @Column(nullable = false)
    int loyaltyPoints; // Điểm thưởng của khách hàng

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    LoyaltyLevel level; // Thứ hạng khách hàng

    @Column(nullable = false)
    LocalDateTime lastUpdated;

    // Cập nhật thời gian khi bản ghi được tạo
    @PrePersist
    protected void onCreate() {
        this.lastUpdated = LocalDateTime.now();
    }

    // Cập nhật thời gian khi bản ghi được chỉnh sửa
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    // Thêm điểm thưởng và cập nhật thứ hạng
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        updateLoyaltyLevel();
    }

    // Giảm điểm thưởng và cập nhật thứ hạng
    public void removeLoyaltyPoints(int points) {
        this.loyaltyPoints = Math.max(this.loyaltyPoints - points, 0); // Không cho phép điểm âm
        updateLoyaltyLevel();
    }

    // Cập nhật thứ hạng dựa trên số điểm thưởng
    private void updateLoyaltyLevel() {
        if (this.loyaltyPoints >= 10000) {
            this.level = LoyaltyLevel.DIAMOND;
        } else if (this.loyaltyPoints >= 5000) {
            this.level = LoyaltyLevel.PLATINUM;
        } else if (this.loyaltyPoints >= 2000) {
            this.level = LoyaltyLevel.GOLD;
        } else if (this.loyaltyPoints >= 1000) {
            this.level = LoyaltyLevel.SILVER;
        } else {
            this.level = LoyaltyLevel.BRONZE;
        }
    }
}
