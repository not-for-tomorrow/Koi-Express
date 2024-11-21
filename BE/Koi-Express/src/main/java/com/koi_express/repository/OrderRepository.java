package com.koi_express.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.koi_express.dto.OrderWithCustomerDTO;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    Page<Orders> findAllByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT new com.koi_express.dto.OrderWithCustomerDTO(o, c, s) " + "FROM Orders o "
            + "JOIN o.customer c "
            + "LEFT JOIN FETCH o.shipment s "
            + "WHERE o.customer.customerId = :customerId "
            + "AND (:status IS NULL OR o.status = :status) "
            + "AND (:fromDate IS NULL OR o.createdAt >= :fromDate) "
            + "AND (:toDate IS NULL OR o.createdAt <= :toDate)")
    List<OrderWithCustomerDTO> findOrdersWithFilters(
            @Param("customerId") Long customerId,
            @Param("status") OrderStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("SELECT o FROM Orders o " + "WHERE o.customer.customerId = :customerId "
            + "AND o.status = 'DELIVERED' "
            + "ORDER BY o.createdAt DESC")

    List<Orders> findByStatusAndDeliveringStaffId(OrderStatus status, Long deliveringStaffId);

    @Query("SELECT new com.koi_express.dto.OrderWithCustomerDTO(o, c, s) " + "FROM Orders o "
            + "JOIN o.customer c "
            + "LEFT JOIN FETCH o.shipment s")
    List<OrderWithCustomerDTO> findAllWithCustomerAndShipment();

    @Query("SELECT new com.koi_express.dto.OrderWithCustomerDTO(o, c, s) " + "FROM Orders o "
            + "JOIN o.customer c "
            + "LEFT JOIN FETCH o.shipment s "
            + "WHERE o.orderId = :orderId")
    Optional<OrderWithCustomerDTO> findOrderWithCustomerAndShipment(@Param("orderId") Long orderId);

    List<Orders> findByStatus(OrderStatus status);

    @Query("SELECT SUM(o.totalFee) FROM Orders o WHERE o.createdAt = :date")
    Optional<BigDecimal> findTotalAmountByDate(@Param("date") LocalDateTime date);

    @Query("SELECT SUM(o.totalFee) FROM Orders o")
    Optional<BigDecimal> findTotalAmount();

    @Query("SELECT SUM(o.totalFee) FROM Orders o WHERE YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month")
    Optional<BigDecimal> findTotalAmountByMonthAndYear(int year, int month);

    List<Orders> findByDeliveringStaffAndStatus(DeliveringStaff deliveringStaff, OrderStatus status);
}
