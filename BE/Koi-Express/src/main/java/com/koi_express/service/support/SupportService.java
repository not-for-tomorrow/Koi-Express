package com.koi_express.service.support;

import com.koi_express.dto.request.SupportCreateRequest;
import com.koi_express.dto.request.SupportRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.customer.Support;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.SupportRequestsStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.SupportRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

    public SupportRequest createSupportRequest(SupportCreateRequest request, Long customerId) {

        Customers customers = customersRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Description cannot be empty");
        }

        Support supportRequest = Support.builder()
                .customer(customers)
                .subject("Nhân viên giao hàng chưa tới lấy đơn hàng")
                .description(request.getDescription())
                .supportRequestsStatus(SupportRequestsStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Support savedRequest = supportRepository.save(supportRequest);

        return new SupportRequest(
                savedRequest.getRequestId(),
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
}
