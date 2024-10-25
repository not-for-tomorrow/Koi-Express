package com.koi_express.service.manager;

import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.enums.Role;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.SystemAccountRepository;
import com.koi_express.service.verification.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemAccount {

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public ApiResponse<?> createSalesStaffAccount(CreateStaffRequest createStaffRequest) {
        if (systemAccountRepository.existsByEmail(createStaffRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(createStaffRequest.getPassword());
        com.koi_express.entity.account.SystemAccount salesStaff = com.koi_express.entity.account.SystemAccount.builder()
                .fullName(createStaffRequest.getFullName())
                .phoneNumber(createStaffRequest.getPhoneNumber())
                .email(createStaffRequest.getEmail())
                .passwordHash(encodedPassword)
                .role(Role.SALES_STAFF)
                .active(true)
                .build();

        systemAccountRepository.save(salesStaff);

        emailService.sendAccountCreatedEmail(salesStaff, createStaffRequest.getPassword(), false);

        return new ApiResponse<>(HttpStatus.OK.value(), "Sales staff account created successfully", salesStaff);
    }

    public List<com.koi_express.entity.account.SystemAccount> getAllAccountsByRole(Role role) {
        return systemAccountRepository.findAllByRole(role);
    }
}
