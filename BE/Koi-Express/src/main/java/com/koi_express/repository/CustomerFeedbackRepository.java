package com.koi_express.repository;

import java.util.List;

import com.koi_express.entity.customer.CustomerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {
    List<CustomerFeedback> findByOrder_OrderId(Long orderId);

    List<CustomerFeedback> findByCustomer_Id(Long customerId);

    List<CustomerFeedback> findByDeliveringStaff_Id(Long deliveringStaffId);

    boolean existsByOrder_OrderIdAndCustomer_Id(Long orderId, Long customerId);
}
