package com.koi_express.repository;

import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesStaffRepository extends JpaRepository<Orders, Long> {

    Page<Orders> findAllByStatus(OrderStatus status, Pageable pageable);
}
