package com.koi_express.service.customer;

import com.koi_express.entity.customer.CustomerFeedback;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.repository.CustomerFeedbackRepository;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.DeliveringStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomerFeedbackService {


    private final CustomerFeedbackRepository feedbackRepository;
    private final OrderRepository orderRepository;
    private final CustomersRepository customersRepository;
    private final DeliveringStaffRepository deliveringStaffRepository;

    public CustomerFeedback submitFeedback(int rating, Set<String> tags, String comments, Long customerId) {
        Customers customer = customersRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Orders order = orderRepository.findCurrentOrderForCustomer(customerId)
                .orElseThrow(() -> new IllegalArgumentException("No active order found for this customer"));

        DeliveringStaff deliveringStaff = deliveringStaffRepository.findByOrderId(order.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("No staff assigned for this order"));

        CustomerFeedback feedback = CustomerFeedback.builder()
                .order(order)
                .customer(customer)
                .deliveringStaff(deliveringStaff)
                .rating(rating)
                .tags(tags)
                .comments(comments)
                .submittedAt(LocalDateTime.now())
                .build();

        return feedbackRepository.save(feedback);
    }

    public List<CustomerFeedback> getFeedbackByOrder(Long orderId) {
        return feedbackRepository.findByOrder_OrderId(orderId);
    }

    public List<CustomerFeedback> getFeedbackByCustomer(Long customerId) {
        return feedbackRepository.findByCustomerId(customerId);
    }

    public List<CustomerFeedback> getFeedbackByDeliveringStaff(Long staffId) {
        return feedbackRepository.findByDeliveringStaffId(staffId);
    }
}
