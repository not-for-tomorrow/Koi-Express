package com.koi_express.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.koi_express.entity.order.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

}
