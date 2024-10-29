package com.koi_express.repository;

import java.util.List;
import java.util.Optional;

import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.DeliveringStaffLevel;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.Role;
import com.koi_express.enums.StaffStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveringStaffRepository extends JpaRepository<DeliveringStaff, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<DeliveringStaff> findByPhoneNumber(String phoneNumber);

    List<DeliveringStaff> findByLevelAndStatus(DeliveringStaffLevel level, StaffStatus status);

    List<DeliveringStaff> findByStatus(StaffStatus status);

    List<DeliveringStaff> findAllByRole(Role role);

    // Find delivering staff with a specific role and status
    List<DeliveringStaff> findAllByRoleAndStatus(Role role, StaffStatus status);

    // Find staff by level, status, and minimum order count
    @Query("SELECT ds FROM DeliveringStaff ds WHERE ds.level = :level AND ds.status = :status " +
            "AND SIZE(ds.ordersReceived) >= :minOrders")
    List<DeliveringStaff> findByLevelAndStatusWithMinOrders(
            @Param("level") DeliveringStaffLevel level,
            @Param("status") StaffStatus status,
            @Param("minOrders") int minOrders);
}
