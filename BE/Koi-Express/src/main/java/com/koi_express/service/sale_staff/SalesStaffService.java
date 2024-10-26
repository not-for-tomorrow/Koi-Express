package com.koi_express.service.sale_staff;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.repository.SalesStaffRepository;
import com.koi_express.service.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SalesStaffService {

    private static final Logger logger = LoggerFactory.getLogger(SalesStaffService.class);

    private final SalesStaffRepository salesStaffRepository;
    private final OrderService orderService;

    public SalesStaffService(SalesStaffRepository salesStaffRepository, OrderService orderService) {
        this.salesStaffRepository = salesStaffRepository;
        this.orderService = orderService;
    }

    public Page<Orders> getPendingOrders(Pageable pageable) {
        logger.info("Fetching pending orders for sales staff.");
        return salesStaffRepository.findAllByStatus(OrderStatus.PENDING, pageable);
    }

    public ApiResponse<String> acceptOrder(Long orderId) {
        try {
            logger.info("Sales staff attempting to accept order with ID: {}", orderId);
            ApiResponse<String> response = orderService.acceptOrder(orderId);
            logger.info("Order with ID {} has been accepted", orderId);
            return response;
        } catch (Exception e) {
            logger.error("Error accepting order with ID: {}", orderId, e);
            return new ApiResponse<>(500, "Error accepting order: " + e.getMessage(), null);
        }
    }
}
