package com.koi_express.repository;

import com.koi_express.entity.account.SystemAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, Long> {

    boolean existsByEmail(String email);

}
