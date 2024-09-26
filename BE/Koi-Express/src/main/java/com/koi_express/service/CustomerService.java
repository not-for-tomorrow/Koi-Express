package com.koi_express.service;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.LoginRequest;
import com.koi_express.dto.request.RegisterRequest;
import com.koi_express.dto.request.UpdateRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.Customers;
import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

        String email = registerRequest.getEmail() != null ? registerRequest.getEmail() : registerRequest.getPhoneNumber() + "@noemail.com";

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        Customers customer = Customers.builder()
                .fullName(registerRequest.getFullName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .email(email)
                .passwordHash(encodedPassword) // Sử dụng sdt làm mật khẩu và mã hóa
                .authProvider(AuthProvider.LOCAL) // Đăng ký bằng số điện thoại nên authProvider là LOCAL
                .role(Role.CUSTOMER) // Mặc định role là CUSTOMER
                .createdAt(LocalDateTime.now()) // Lưu thời gian đăng ký
                .build();

        customersRepository.save(customer);
        return new ApiResponse<>(HttpStatus.OK.value(), "User registration successfully", customer);

    }

    public ApiResponse<String> authenticateCustomer(LoginRequest loginRequest) {
        Customers customer = customersRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Invalid phone number"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), customer.getPasswordHash())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        String token = jwtUtil.generateToken(customer.getPhoneNumber(), "Koi-Express", customer.getRole().name());

        return new ApiResponse<>(HttpStatus.OK.value(), "Login successfully", token);
    }

    public Customers getCustomerDetails(String phoneNumber) {
        return customersRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    public ApiResponse<Customers> updateCustomer(Long id, UpdateRequest updateRequest) {
        Customers customer = customersRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        customer.setFullName(updateRequest.getFullName());
        customer.setAddress(updateRequest.getAddress());

        customersRepository.save(customer);
        return new ApiResponse<>(HttpStatus.OK.value(), "Customer updated successfully", customer);
    }



}
