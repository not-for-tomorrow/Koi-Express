package com.koi_express.repository;

import com.koi_express.entity.customer.CustomerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {
    List<CustomerFeedback> findByOrder_OrderId(Long orderId);
    List<CustomerFeedback> findByCustomerId(Long customerId);
    List<CustomerFeedback> findByDeliveringStaffId(Long deliveringStaffId);
}
