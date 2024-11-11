package com.koi_express.service.customer;

import java.time.LocalDateTime;
import java.util.Optional;

import com.koi_express.dto.request.LoginRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.account.SystemAccount;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.customer.User;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.DeliveringStaffRepository;
import com.koi_express.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomersRepository customersRepository;
    private final SystemAccountRepository systemAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final DeliveringStaffRepository deliveringStaffRepository;

    public ApiResponse<String> authenticateUser(LoginRequest loginRequest) {
        Optional<? extends User> userOptional = findUserByPhoneNumber(loginRequest.getPhoneNumber());

        User user = userOptional.orElseThrow(() -> new RuntimeException("Invalid phone number"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        if (user instanceof Customers) {
            Customers customer = (Customers) user;
            customer.setLastLogin(LocalDateTime.now());
            customersRepository.save(customer);
        }

        String token = jwtUtil.generateToken(
                user.getPhoneNumber(),
                "Koi-Express",
                user.getRole().name(),
                user.getId().toString(),
                user.getFullName(),
                user.getEmail());

        return new ApiResponse<>(HttpStatus.OK.value(), "Login successfully", token);
    }

    private Optional<? extends User> findUserByPhoneNumber(String phoneNumber) {
        Optional<Customers> customer = customersRepository.findByPhoneNumber(phoneNumber);
        if (customer.isPresent()) {
            return customer;
        }

        Optional<SystemAccount> systemAccount = systemAccountRepository.findByPhoneNumber(phoneNumber);
        if (systemAccount.isPresent()) {
            return systemAccount;
        }

        return deliveringStaffRepository.findByPhoneNumber(phoneNumber);
    }
}
