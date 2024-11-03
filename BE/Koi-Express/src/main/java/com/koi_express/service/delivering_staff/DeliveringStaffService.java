package com.koi_express.service.delivering_staff;

import java.time.LocalDateTime;
import java.util.List;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.entity.shipment.Shipments;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.ShipmentStatus;
import com.koi_express.enums.StaffStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.DeliveringStaffRepository;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.ShipmentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveringStaffService {

    private final OrderRepository orderRepository;
    private final ShipmentsRepository shipmentsRepository;
    private final PickupTimeCalculator pickupTimeCalculator;
    private final DeliveringStaffRepository deliveringStaffRepository;

    @Transactional(readOnly = true)
    public List<Orders> getAssignedOrdersByDeliveringStaff(Long deliveringStaffId) {
        log.info("Fetching orders assigned to delivering staff with ID: {}", deliveringStaffId);
        return orderRepository.findByStatusAndDeliveringStaffId(OrderStatus.ASSIGNED, deliveringStaffId);
    }

    @Transactional(readOnly = true)
    public List<Orders> getPickupOrdersByDeliveringStaff(Long deliveringStaffId) {
        log.info("Fetching orders assigned to delivering staff with ID: {}", deliveringStaffId);
        return orderRepository.findByStatusAndDeliveringStaffId(OrderStatus.PICKING_UP, deliveringStaffId);
    }

    @Transactional
    public ApiResponse<String> pickupOrder(Long orderId, Long deliveringStaffId) {
        log.info("Attempting to pick up order with ID: {} by staff with ID: {}", orderId, deliveringStaffId);

        Orders order = validateOrderAssignment(orderId, deliveringStaffId);

        order.setStatus(OrderStatus.PICKING_UP);
        orderRepository.save(order);

        LocalDateTime pickupTime =
                pickupTimeCalculator.calculatePickupTime(order.getOrderDetail().getKilometers());

        Shipments shipment = Shipments.builder()
                .customer(order.getCustomer())
                .order(order)
                .deliveringStaff(order.getDeliveringStaff())
                .status(ShipmentStatus.PREPARING)
                .estimatedPickupTime(pickupTime)
                .build();

        shipmentsRepository.save(shipment);

        log.info("Order ID: {} status updated to Picking Up by staff ID: {}", orderId, deliveringStaffId);
        log.info("Shipment created for order ID: {}", orderId);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order status updated to Picking Up", null);
    }

    @Transactional
    public ApiResponse<String> completeOrder(Long orderId, Long deliveringStaffId) {
        log.info("Completing order ID: {} for delivering staff ID: {}", orderId, deliveringStaffId);

        Orders order = validateOrderAssignment(orderId, deliveringStaffId);

        // Update the order status to DELIVERED
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        // Update the delivering staff status to AVAILABLE if this is their only active order
        DeliveringStaff staff = order.getDeliveringStaff();
        if (!orderRepository.existsByStatusAndDeliveringStaff_StaffId(OrderStatus.PICKING_UP, deliveringStaffId)) {
            staff.setStatus(StaffStatus.AVAILABLE);
            deliveringStaffRepository.save(staff);
            log.info("Delivering staff ID: {} status updated to AVAILABLE", deliveringStaffId);
        }

        log.info("Order ID: {} marked as DELIVERED by staff ID: {}", orderId, deliveringStaffId);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order marked as delivered", null);
    }

    private Orders validateOrderAssignment(Long orderId, Long deliveringStaffId) {
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("Order with ID: {} not found", orderId);
            return new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + orderId);
        });

        if (!order.getDeliveringStaff().getStaffId().equals(deliveringStaffId)) {
            log.error("Order ID: {} is not assigned to staff ID: {}", orderId, deliveringStaffId);
            throw new AppException(ErrorCode.ORDER_NOT_ASSIGNED, "Order is not assigned to this staff member");
        }

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            log.error("Order ID: {} is not in the required status, current status: {}", orderId, order.getStatus());
            throw new AppException(ErrorCode.ORDER_ALREADY_PROCESSED, "Order is not in the required status");
        }

        return order;
    }
}
