package com.koi_express.service.deliveringStaff;

import java.util.List;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveringStaffService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Orders> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Orders> getAssignedOrdersByDeliveringStaff(Long deliveringStaffId) {
        return orderRepository.findByStatusAndDeliveringStaffId(OrderStatus.ASSIGNED, deliveringStaffId);
    }

    @Transactional
    public ApiResponse<String> pickupOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_PROCESSED, "Order is not in ASSIGNED status");
        }

        order.setStatus(OrderStatus.PICKING_UP);
        orderRepository.save(order);

        return new ApiResponse<>(HttpStatus.OK.value(), "Order status updated to Picking Up", null);
    }
}
