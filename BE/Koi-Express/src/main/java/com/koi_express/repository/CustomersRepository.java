package com.koi_express.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.AuthProvider;
import lombok.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
