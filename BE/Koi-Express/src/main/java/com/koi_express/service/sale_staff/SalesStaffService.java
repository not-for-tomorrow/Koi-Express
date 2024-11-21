package com.koi_express.service.sale_staff;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Support;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.SupportRequestsStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.SupportRepository;
import com.koi_express.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesStaffService {

    private static final Logger logger = LoggerFactory.getLogger(SalesStaffService.class);

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final SupportRepository supportRepository;

    public Page<Orders> getPendingOrders(Pageable pageable) {
        logger.info("Fetching pending orders for sales staff.");
        return orderRepository.findAllByStatus(OrderStatus.PENDING, pageable);
    }

    public ApiResponse<String> acceptOrder(Long orderId) {

        Orders order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.ACCEPTED || order.getStatus() == OrderStatus.ASSIGNED) {
            return new ApiResponse<>(400, "Order has already been accepted or assigned", null);
        }

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

    public Page<Support> getPendingSupport(Pageable pageable) {
        logger.info("Fetching pending support requests for sales staff.");
        return supportRepository.findAllBySupportRequestsStatus(SupportRequestsStatus.PENDING, pageable);
    }
}
