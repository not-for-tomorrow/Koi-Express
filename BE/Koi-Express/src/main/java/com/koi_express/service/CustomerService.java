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

    public Page<Customers> getAllCustomers(Pageable pageable) {
        return customersRepository.findAll(pageable);
    }


    public boolean deleteCustomer(Long id) {
        if(!customersRepository.existsById(id)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_FOUND);
        }

        customersRepository.deleteById(id);
        return true;
    }

    public Customers getCustomerById(Long customerId){
        return customersRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Couldn't find customer'"));
    }

    public Customers findByPhoneNumber(String phoneNumber) {
        Optional<Customers> customerOptional = customersRepository.findByPhoneNumber(phoneNumber);
        return customerOptional.orElseThrow(()
                -> new RuntimeException("Couldn't find'"));
    }

    public Customers findByEmail(String email) {
        Optional<Customers> customerOptional = customersRepository.findByEmail(email);
        return customerOptional.orElse(null);
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
