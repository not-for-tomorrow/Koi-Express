package com.koi_express.service.support;

import com.koi_express.dto.request.SupportCreateRequest;
import com.koi_express.dto.request.SupportRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.customer.Support;
import com.koi_express.enums.SupportRequestsStatus;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.SupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportRepository supportRepository;
    private final CustomersRepository customersRepository;

    public SupportRequest createSupportRequest(SupportCreateRequest request, Long customerId) {

        Customers customers = customersRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

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
}
