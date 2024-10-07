package com.koi_express.repository;

import com.koi_express.entity.account.SystemAccount;
import com.koi_express.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, Long> {

    boolean existsByEmail(String email);

    Page<SystemAccount> findAllByRole(Role role, Pageable pageable);

    Optional<SystemAccount> findByPhoneNumber(String phoneNumber);

}
