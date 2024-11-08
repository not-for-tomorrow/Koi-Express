package com.koi_express.service.customer;

import com.koi_express.entity.customer.CustomerFeedback;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.FeedbackTag;
import com.koi_express.enums.OrderStatus;
import com.koi_express.repository.CustomerFeedbackRepository;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.DeliveringStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerFeedbackService {

    private final CustomerFeedbackRepository feedbackRepository;
    private final OrderRepository orderRepository;
    private final CustomersRepository customersRepository;
    private final DeliveringStaffRepository deliveringStaffRepository;

    public boolean isOrderDelivered(Long orderId) {
        Orders order = orderRepository.findById(orderId).orElse(null);
        return order != null && OrderStatus.DELIVERED.equals(order.getStatus());
    }

    public CustomerFeedback submitFeedback(int rating, Set<FeedbackTag> tags, String comments, Long customerId) {
        Customers customer = customersRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Orders order = orderRepository.findDeliveredOrderForCustomer(customerId)
                .orElseThrow(() -> new IllegalArgumentException("No delivered order found for this customer"));

        if (!isOrderDelivered(order.getOrderId())) {
            throw new IllegalArgumentException("Feedback can only be submitted for delivered orders");
        }

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
        return feedbackRepository.findByCustomer_Id(customerId);
    }

    public List<CustomerFeedback> getFeedbackByDeliveringStaff(Long staffId) {
        return feedbackRepository.findByDeliveringStaff_Id(staffId);
    }
}
