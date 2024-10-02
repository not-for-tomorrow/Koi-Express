package com.koi_express.entity;

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
public class StaffTraining { // quán lý đào tạo nhân viên

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    Customers staff;

    @Column(nullable = false)
    String trainingModule;

    @Column(nullable = false)
    LocalDateTime trainingDate;

    @Column(nullable = false)
    boolean completed;

    String certificateUrl;
}
