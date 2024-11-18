package com.koi_express.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.koi_express.dto.request.CustomerTopSpenderRequest;
import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.AuthProvider;
import lombok.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Long> {

    Optional<Customers> findByEmailAndAuthProvider(String email, AuthProvider authProvider);

    Optional<Customers> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @NonNull
    List<Customers> findAll(@NonNull Sort sort);

    List<Customers> findByActiveTrueAndLastLoginBefore(LocalDateTime date);

    @Query(
            "SELECT new com.koi_express.dto.request.CustomerTopSpenderRequest(c.customerId, c.fullName, SUM(o.totalFee), COUNT(o)) "
                    + "FROM Customers c JOIN Orders o ON o.customer = c "
                    + "WHERE o.status = com.koi_express.enums.OrderStatus.DELIVERED "
                    + "GROUP BY c.customerId, c.fullName, c.email "
                    + "ORDER BY SUM(o.totalFee) DESC")
    List<CustomerTopSpenderRequest> findTop10CustomersByTotalSpent();
}
