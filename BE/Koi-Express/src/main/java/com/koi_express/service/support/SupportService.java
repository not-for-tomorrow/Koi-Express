package com.koi_express.service.support;

import com.koi_express.dto.OrderWithCustomerDTO;
import com.koi_express.dto.request.SupportCreateRequest;
import com.koi_express.dto.request.SupportRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.account.SystemAccount;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.customer.Support;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.StaffStatus;
import com.koi_express.enums.SupportRequestsStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.DeliveringStaffRepository;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.SupportRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupportService {

    private static final Logger logger = LoggerFactory.getLogger(SupportService.class);
    private static final String SUPPORT_NOT_FOUND_MESSAGE = "Support not found";

    private final SupportRepository supportRepository;
    private final CustomersRepository customersRepository;
    private final DeliveringStaffRepository deliveringStaffRepository;
    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;

    public SupportRequest createSupportRequest(SupportCreateRequest request, Long customerId, Long orderId) {

        Customers customers = customersRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Description cannot be empty");
        }

        Support supportRequest = Support.builder()
                .customer(customers)
                .order(order)
                .subject("Nhân viên giao hàng chưa tới lấy đơn hàng")
                .description(request.getDescription())
                .supportRequestsStatus(SupportRequestsStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Support savedRequest = supportRepository.save(supportRequest);

        return new SupportRequest(
                savedRequest.getRequestId(),
                savedRequest.getOrder().getOrderId(),
                savedRequest.getCustomer().getCustomerId(),
                savedRequest.getSubject(),
                savedRequest.getDescription(),
                savedRequest.getSupportRequestsStatus().name(),
                savedRequest.getCreatedAt()
        );
    }

    public ApiResponse<List<Support>> getAllSupport() {
        return new ApiResponse<>(HttpStatus.OK.value(), "Customers fetched successfully.", supportRepository.findAll());
    }

    public ApiResponse<String> acceptSupport(Long requestId) {
        Support support = supportRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPORT_REQUEST_NOT_FOUND, "Support not found with ID: " + requestId));

        logger.info("Accepting support request with ID: {}", requestId);

        if (support.getSupportRequestsStatus() != SupportRequestsStatus.PENDING) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Support request already in progress or resolved", null);
        }

        support.setSupportRequestsStatus(SupportRequestsStatus.IN_PROGRESS);
        supportRepository.save(support);

        return new ApiResponse<>(HttpStatus.OK.value(), "Support request accepted successfully", null);
    }

    public ApiResponse<String> cancelDeliveryStaff(Long requestId) {
        Support support = supportRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPORT_REQUEST_NOT_FOUND, SUPPORT_NOT_FOUND_MESSAGE));

        if (support.getSupportRequestsStatus() == SupportRequestsStatus.RESOLVED) {
            throw new AppException(ErrorCode.SUPPORT_REQUEST_ALREADY_RESOLVED, "Support request is already resolved");
        }

        Orders orders = support.getOrder();
        if(orders == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found");
        }

        DeliveringStaff deliveringStaff = orders.getDeliveringStaff();
        if (deliveringStaff == null) {
            throw new AppException(ErrorCode.DELIVERING_STAFF_NOT_FOUND, "Delivering staff not found");
        }

        SystemAccount systemAccount = support.getStaff();
        if (systemAccount == null) {
            throw new AppException(ErrorCode.SYSTEM_ACCOUNT_NOT_FOUND, "System account not found");
        }


        deliveringStaff.setActive(false);
        deliveringStaff.setStatus(StaffStatus.BANNED_FOR_ONE_WEEK);
        deliveringStaffRepository.save(deliveringStaff);

        orders.setStatus(OrderStatus.PENDING);
        orders.setDeliveringStaff(null);
        orderRepository.save(orders);

        support.setSupportRequestsStatus(SupportRequestsStatus.RESOLVED);
        support.setResolvedAt(LocalDateTime.now());
        support.setStaffName(support.getStaff().getFullName());
        supportRepository.save(support);

        return new ApiResponse<>(HttpStatus.OK.value(), "Delivering staff banned for one week", null);
    }

    public List<Support> getSupportHistory(Long customerId) {
        return supportRepository.findAllByCustomer_CustomerId(customerId);
    }
}
