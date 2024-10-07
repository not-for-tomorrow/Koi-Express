package com.koi_express.repository;

import com.koi_express.entity.account.SystemAccount;
import com.koi_express.entity.shipment.DeliveringStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveringStaffRepository extends JpaRepository<DeliveringStaff, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<DeliveringStaff> findByPhoneNumber(String phoneNumber);

}
