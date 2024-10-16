package com.koi_express.repository;

import com.koi_express.entity.audit.TransactionLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLogsRepository extends JpaRepository<TransactionLogs, Long> {
}
