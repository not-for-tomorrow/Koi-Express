package com.koi_express.repository;

import com.koi_express.entity.account.Staff;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.promotion.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByCustomerAndStaffAndTimestampAfter(
            Customers customer, Staff staff, LocalDateTime timestamp
    );
}
