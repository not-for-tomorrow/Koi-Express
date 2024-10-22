package com.koi_express.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    Page<Orders> findAll(Pageable pageable);

    List<Orders> findByStatus(OrderStatus status);

    // Tìm đơn hàng theo khách hàng
    List<Orders> findByCustomerCustomerId(Long customerId);

    List<Orders> findByCustomerCustomerIdAndStatus(Long customerId, OrderStatus status);

    @Query("SELECT o FROM Orders o WHERE o.customer.customerId = :customerId "
            + "AND (:status IS NULL OR o.status = :status) "
            + "AND (:fromDate IS NULL OR o.createdAt >= :fromDate) "
            + "AND (:toDate IS NULL OR o.createdAt <= :toDate)")
    List<Orders> findOrdersWithFilters(
            @Param("customerId") Long customerId,
            @Param("status") OrderStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    List<Orders> findByStatusAndDeliveringStaffId(OrderStatus status, Long deliveringStaffId);

    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.orderDetail WHERE o.orderId = :orderId")
    Optional<Orders> findByIdWithDetail(@Param("orderId") Long orderId);
}
