package com.koi_express.service.manager;

import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.account.SystemAccount;
import com.koi_express.enums.Role;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.SystemAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SystemAccountService {

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ApiResponse<?> createSalesStaffAccount(CreateStaffRequest createStaffRequest) {
        if (systemAccountRepository.existsByEmail(createStaffRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(createStaffRequest.getPassword());
        SystemAccount salesStaff = SystemAccount.builder()
                .fullName(createStaffRequest.getFullName())
                .phoneNumber(createStaffRequest.getPhoneNumber())
                .email(createStaffRequest.getEmail())
                .passwordHash(encodedPassword)
                .role(Role.SALES_STAFF)
                .active(true)
                .build();

        systemAccountRepository.save(salesStaff);
        return new ApiResponse<>(HttpStatus.OK.value(), "Sales staff account created successfully", salesStaff);
    }

    public Page<SystemAccount> getAllAccountsByRole(Role role, Pageable pageable) {
        return systemAccountRepository.findAllByRole(role, pageable);
    }
}
