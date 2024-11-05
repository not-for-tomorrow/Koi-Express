package com.koi_express.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.koi_express.dto.OrderWithCustomerDTO;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT new com.koi_express.dto.OrderWithCustomerDTO(o, c, s) " +
            "FROM Orders o " +
            "JOIN o.customer c " +
            "LEFT JOIN FETCH o.shipment s " +
            "WHERE o.customer.customerId = :customerId " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:fromDate IS NULL OR o.createdAt >= :fromDate) " +
            "AND (:toDate IS NULL OR o.createdAt <= :toDate)")
    List<OrderWithCustomerDTO> findOrdersWithFilters(
            @Param("customerId") Long customerId,
            @Param("status") OrderStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("SELECT o FROM Orders o " +
            "WHERE o.customer.customerId = :customerId " +
            "AND o.status = 'IN_PROGRESS' " + // or another relevant status
            "ORDER BY o.createdAt DESC")
    Optional<Orders> findCurrentOrderForCustomer(@Param("customerId") Long customerId);


    List<Orders> findByStatusAndDeliveringStaffId(OrderStatus status, Long deliveringStaffId);

    @Query("SELECT new com.koi_express.dto.OrderWithCustomerDTO(o, c, s) " +
            "FROM Orders o " +
            "JOIN o.customer c " +
            "LEFT JOIN FETCH o.shipment s")
    List<OrderWithCustomerDTO> findAllWithCustomerAndShipment();

    @Query("SELECT new com.koi_express.dto.OrderWithCustomerDTO(o, c, s) " +
            "FROM Orders o " +
            "JOIN o.customer c " +
            "LEFT JOIN FETCH o.shipment s " +
            "WHERE o.orderId = :orderId")
    Optional<OrderWithCustomerDTO> findOrderWithCustomerAndShipment(@Param("orderId") Long orderId);

    boolean existsByStatusAndDeliveringStaff_StaffId(OrderStatus status, Long deliveringStaffId);

    @Query("SELECT SUM(o.totalFee) FROM Orders o WHERE o.createdAt = :date")
    Optional<BigDecimal> findTotalRevenueByDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(o.totalFee) FROM Orders o WHERE YEAR(o.createdAt) = :year AND WEEK(o.createdAt) = :week")
    Optional<BigDecimal> findTotalRevenueByWeek(@Param("year") int year, @Param("week") int week);

    @Query("SELECT SUM(o.totalFee) FROM Orders o WHERE YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month")
    Optional<BigDecimal> findTotalRevenueByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(o.totalFee) FROM Orders o WHERE YEAR(o.createdAt) = :year")
    Optional<BigDecimal> findTotalRevenueByYear(@Param("year") int year);

    @Query("SELECT o.customer FROM Orders o GROUP BY o.customer ORDER BY COUNT(o) DESC LIMIT 1")
    Optional<Customers> findCustomerWithMostOrders();

    @Query("SELECT o.createdAt FROM Orders o GROUP BY o.createdAt ORDER BY SUM(o.totalFee) DESC LIMIT 1")
    Optional<LocalDate> findHighestRevenueDay();

    @Query("SELECT YEAR(o.createdAt), MONTH(o.createdAt) FROM Orders o GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) ORDER BY SUM(o.totalFee) DESC LIMIT 1")
    Optional<YearMonth> findHighestRevenueMonth();

    @Query("SELECT YEAR(o.createdAt) FROM Orders o GROUP BY YEAR(o.createdAt) ORDER BY SUM(o.totalFee) DESC LIMIT 1")
    Optional<Integer> findHighestRevenueYear();

    List<Orders> findByStatus(OrderStatus status);

}
