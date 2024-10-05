package com.koi_express.service.Manager;

import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.DeliveringStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DeliveringStaffService {

    @Autowired
    private DeliveringStaffRepository deliveringStaffRepository;

    public ApiResponse<?> createDeliveringStaffAccount(CreateStaffRequest createStaffRequest) {
        if (deliveringStaffRepository.existsByPhoneNumber(createStaffRequest.getPhoneNumber())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        DeliveringStaff deliveringStaff = DeliveringStaff.builder()
                .fullName(createStaffRequest.getFullName())
                .phoneNumber(createStaffRequest.getPhoneNumber())
                .address(createStaffRequest.getAddress())
                .averageRating(0.0)
                .active(true)
                .currentlyDelivering(false)
                .build();

        deliveringStaffRepository.save(deliveringStaff);
        return new ApiResponse<>(HttpStatus.OK.value(), "Delivering staff account created successfully", deliveringStaff);
    }
}
