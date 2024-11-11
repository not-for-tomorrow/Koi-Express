package com.koi_express.service.sale_staff;

import java.time.LocalDateTime;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.SalesStaffRepository;
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

    private final SalesStaffRepository salesStaffRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public Page<Orders> getPendingOrders(Pageable pageable) {
        logger.info("Fetching pending orders for sales staff.");
        return salesStaffRepository.findAllByStatus(OrderStatus.PENDING, pageable);
    }

    public ApiResponse<String> acceptOrder(Long orderId) {

        Orders order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.ACCEPTED || order.getStatus() == OrderStatus.ASSIGNED) {
            return new ApiResponse<>(400, "Order has already been accepted or assigned", null);
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime createdAt = order.getCreatedAt();
        long hoursSinceOrder =
                java.time.Duration.between(createdAt, currentTime).toHours();

        if (hoursSinceOrder < 12) {
            logger.warn(
                    "Order with ID {} cannot be accepted yet. Only {} hours have passed since creation.",
                    orderId,
                    hoursSinceOrder);
            return new ApiResponse<>(400, "Order can only be accepted 12 hours after creation", null);
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
}
