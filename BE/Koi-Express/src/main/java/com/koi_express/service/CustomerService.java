package com.koi_express.service;

import com.koi_express.dto.request.LoginRequest;
import com.koi_express.dto.request.RegisterRequest;
import com.koi_express.entity.Customers;
import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import com.koi_express.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    private final CustomersRepository customersRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor-based injection for better testability
    public CustomerService(CustomersRepository customersRepository, PasswordEncoder passwordEncoder) {
        this.customersRepository = customersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerCustomer(RegisterRequest registerRequest) {
        if (customersRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            return "Error: Phone number is already in use!";
        }

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
        return "User registered successfully!";
    }

    public String authenticateCustomer(LoginRequest loginRequest) {
        Customers customer = customersRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElse(null);

        if (customer == null) {
            return "Error: Invalid phone number!";
        }

        // Compare the stored password hash with the entered password
        if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPasswordHash())) {
            return "Error: Invalid password!";
        }

        return "Login successful!";
    }

    public List<Customers> getAllCustomers() {
        return customersRepository.findAll();
    }
}
