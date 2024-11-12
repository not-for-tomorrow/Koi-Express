package com.koi_express.repository;

import java.util.List;
import java.util.Optional;

import com.koi_express.entity.account.SystemAccount;
import com.koi_express.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, Long> {

    boolean existsByEmail(String email);

    List<SystemAccount> findAllByRole(Role role);

    Optional<SystemAccount> findByPhoneNumber(String phoneNumber);
}
