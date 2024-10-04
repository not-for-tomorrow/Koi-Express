package com.koi_express.service;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.Customers;
import com.koi_express.entity.DeliveringStaff;
import com.koi_express.entity.SystemAccount;
import com.koi_express.enums.Role;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.DeliveringStaffRepository;
import com.koi_express.repository.ManagerRepository;
import com.koi_express.repository.SystemAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    private final ManagerRepository managerRepository;
    private final SystemAccountRepository systemAccountRepository;
    private final DeliveringStaffRepository deliveringStaffRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ManagerService(ManagerRepository managerRepository, SystemAccountRepository systemAccountRepository, DeliveringStaffRepository deliveringStaffRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.systemAccountRepository = systemAccountRepository;
        this.deliveringStaffRepository = deliveringStaffRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Customers> getAllCustomers(Pageable pageable, String token) {

        String customerId = jwtUtil.extractCustomerId(token);
        logger.info("Extracted customerId: {}", customerId);

        return managerRepository.findAll(pageable);
    }

    public Customers findByPhoneNumber(String phoneNumber) {
        Optional<Customers> customerOptional = managerRepository.findByPhoneNumber(phoneNumber);
        return customerOptional.orElseThrow(()
                -> new RuntimeException("Couldn't find'"));
    }

    public boolean deleteCustomer(Long id) {

        if(!managerRepository.existsById(id)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_FOUND);
        }

        managerRepository.deleteById(id);
        return true;
    }

    public Customers getCustomerById(Long customerId) {

        return managerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    public Customers updateCustomer(Long id, String fullName, String address) {

        Customers customer = managerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        customer.setFullName(fullName);
        customer.setAddress(address);
        return managerRepository.save(customer);
    }

    public ApiResponse<?> createStaffAccount(CreateStaffRequest createStaffRequest) {

        if(createStaffRequest.getRole() == Role.SALES_STAFF) {

            if(systemAccountRepository.existsByEmail(createStaffRequest.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }

            String encodedPassword = passwordEncoder.encode(createStaffRequest.getPassword());
            SystemAccount salesStaff = SystemAccount.builder()
                    .fullName(createStaffRequest.getFullName())
                    .email(createStaffRequest.getEmail())
                    .passwordHash(encodedPassword)
                    .role(createStaffRequest.getRole())
                    .role(Role.SALES_STAFF)
                    .active(true)
                    .build();

            systemAccountRepository.save(salesStaff);
            return new ApiResponse<>(HttpStatus.OK.value(), "Sales staff account created successfully", salesStaff);
        } else if(createStaffRequest.getRole() == Role.DELIVERING_STAFF) {

            if(deliveringStaffRepository.existsByPhoneNumber(createStaffRequest.getPhoneNumber())) {
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

        } else {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }
    }
}
