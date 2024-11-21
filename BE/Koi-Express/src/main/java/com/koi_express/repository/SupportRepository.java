package com.koi_express.repository;

import com.koi_express.entity.customer.Support;
import com.koi_express.enums.SupportRequestsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {

    Page<Support> findAllBySupportRequestsStatus(SupportRequestsStatus requestsStatus, Pageable pageable);
}
