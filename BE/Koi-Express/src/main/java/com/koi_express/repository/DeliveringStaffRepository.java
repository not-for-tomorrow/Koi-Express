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
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveringStaffRepository extends JpaRepository<DeliveringStaff, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<DeliveringStaff> findByPhoneNumber(String phoneNumber);

    List<DeliveringStaff> findByLevelAndStatus(DeliveringStaffLevel level, StaffStatus status);

    List<DeliveringStaff> findByStatus(StaffStatus status);

    List<DeliveringStaff> findAllByRole(Role role);
}
