package com.koi_express.service.manager;

import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.enums.DeliveringStaffLevel;
import com.koi_express.enums.Role;
import com.koi_express.enums.StaffStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.DeliveringStaffRepository;
import com.koi_express.service.verification.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveringStaff {

    private final DeliveringStaffRepository deliveringStaffRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public DeliveringStaff(DeliveringStaffRepository deliveringStaffRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.deliveringStaffRepository = deliveringStaffRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public ApiResponse<String> createDeliveringStaffAccount(CreateStaffRequest createStaffRequest) {
        if (deliveringStaffRepository.existsByPhoneNumber(createStaffRequest.getPhoneNumber())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        String encodedPassword = passwordEncoder.encode(createStaffRequest.getPassword());

        com.koi_express.entity.shipment.DeliveringStaff deliveringStaff =
                com.koi_express.entity.shipment.DeliveringStaff.builder()
                        .fullName(createStaffRequest.getFullName())
                        .phoneNumber(createStaffRequest.getPhoneNumber())
                        .email(createStaffRequest.getEmail())
                        .address(createStaffRequest.getAddress())
                        .passwordHash(encodedPassword)
                        .averageRating(0.0)
                        .active(true)
                        .status(StaffStatus.AVAILABLE)
                        .level(DeliveringStaffLevel.LEVEL_1)
                        .role(Role.DELIVERING_STAFF)
                        .build();

        deliveringStaffRepository.save(deliveringStaff);

        emailService.sendAccountCreatedEmail(deliveringStaff, createStaffRequest.getPassword(), true);

        return new ApiResponse<>(HttpStatus.CREATED.value(), "Delivering staff account created successfully", null);
    }

    public List<com.koi_express.entity.shipment.DeliveringStaff> getAllAccountsByRole(Role role) {
        return deliveringStaffRepository.findAllByRole(role);
    }
}
