package com.koi_express.service.customer;

import java.time.LocalDateTime;
import java.util.Optional;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.RegisterRequest;
import com.koi_express.dto.request.UpdateRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomersRepository customersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public CustomerService(CustomersRepository customersRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.customersRepository = customersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ApiResponse<Customers> registerCustomer(RegisterRequest registerRequest) {

        if (customersRepository.existsByPhoneNumber(registerRequest.getPhoneNumber()))
            throw new AppException(ErrorCode.USER_EXISTED);

        if (registerRequest.getEmail() != null && customersRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        Customers customer = Customers.builder()
                .fullName(registerRequest.getFullName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .email(registerRequest.getEmail())
                .passwordHash(encodedPassword)
                .authProvider(AuthProvider.LOCAL)
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();

        customersRepository.save(customer);
        return new ApiResponse<>(HttpStatus.OK.value(), "User registration successfully", customer);
    }

    public Customers getCustomerDetails(String phoneNumber) {
        return customersRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    public ApiResponse<Customers> updateCustomer(Long id, UpdateRequest updateRequest) {
        Customers customer =
                customersRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        customer.setFullName(updateRequest.getFullName());
        customer.setEmail(updateRequest.getEmail());

        customersRepository.save(customer);
        return new ApiResponse<>(HttpStatus.OK.value(), "Customer updated successfully", customer);
    }

    public void activateCustomerAccount(String phoneNumber) {

        Optional<Customers> customersOptional = customersRepository.findByPhoneNumber(phoneNumber);

        if (customersOptional.isPresent()) {
            Customers customers = customersOptional.get();
            customers.setActivated(true);
            customersRepository.save(customers);
        }
    }
}
