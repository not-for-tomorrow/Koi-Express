package com.koi_express.service.saleStaff;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.SalesStaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SalesStaffService {

    private static final Logger logger = LoggerFactory.getLogger(SalesStaffService.class);


    @Autowired
    private SalesStaffRepository salesStaffRepository;

    public Page<Orders> getPendingOrders(Pageable pageable) {
        return salesStaffRepository.findAllByStatus(OrderStatus.PENDING, pageable);
    }

    public ApiResponse<String> acceptOrder(Long orderId) {

        Orders orders = salesStaffRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND_CUSTOMER));

        if(orders.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_ALREADY_PROCESSED_CUSTOMER, "Order cannot be accepted in its current state");
        }

        orders.setStatus(OrderStatus.ACCEPTED);
        salesStaffRepository.save(orders);

        logger.info("Order accepted successfully: {}", orders);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order accepted successfully", null);
    }
}
