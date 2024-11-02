package com.koi_express.service.customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.koi_express.dto.request.RegisterRequest;
import com.koi_express.dto.request.UpdateRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.CustomersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomersRepository customersRepository;
    private final PasswordEncoder passwordEncoder;

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
                .active(true)
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

    public Optional<Customers> findByEmailAndAuthProvider(String email, AuthProvider authProvider) {
        return customersRepository.findByEmailAndAuthProvider(email, authProvider);
    }

    public Customers save(Customers customer) {
        return customersRepository.save(customer);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void lockInactiveCustomers() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Customers> inactiveCustomers = customersRepository.findByActiveTrueAndLastLoginBefore(sevenDaysAgo);

        for (Customers customer : inactiveCustomers) {
            customer.setActive(false);
            customersRepository.save(customer);
        }
    }

    public ApiResponse<String> reactivateCustomer(Long customerId) {
        Customers customer = customersRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        if (!customer.isActive()) {
            customer.setActive(true);
            customer.setLastLogin(LocalDateTime.now());
            customersRepository.save(customer);
            return new ApiResponse<>(HttpStatus.OK.value(), "Account reactivated successfully");
        }
        return new ApiResponse<>(HttpStatus.OK.value(), "Account is already active");
    }

    public Customers getCustomerById(Long customerId) {
        return customersRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

}
