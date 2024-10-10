package com.koi_express.repository;

import java.util.List;

import com.koi_express.entity.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByCustomerCustomerId(Long customerId);
}
