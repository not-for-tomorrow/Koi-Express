package com.koi_express.repository;

import com.koi_express.entity.Customers;
import com.koi_express.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Long> {

    Optional<Customers> findByEmail(String email);

    Optional<Customers> findByEmailAndAuthProvider(String email, AuthProvider authProvider);

}
