package com.koi_express.repository;

import com.koi_express.entity.customer.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Customers, Long> {

    List<Customers> findByRole(String role);

    Optional<Customers> findByPhoneNumber(String phoneNumber);

}
