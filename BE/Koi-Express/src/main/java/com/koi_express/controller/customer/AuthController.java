package com.koi_express.controller.customer;

import java.security.SecureRandom;
import java.util.stream.Collectors;

import com.koi_express.dto.request.LoginRequest;
import com.koi_express.dto.request.RegisterRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.service.customer.AuthService;
import com.koi_express.service.customer.CustomerService;
import com.koi_express.service.verification.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final CustomerService customerService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final CustomersRepository customersRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerCustomer(
            @RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        String formattedPhoneNumber = otpService.formatPhoneNumber(registerRequest.getPhoneNumber());

        String otp = otpService.generateOtpForPurpose(formattedPhoneNumber, "REGISTER");
        otpService.sendOtp(formattedPhoneNumber, otp);

        logger.info("Generated OTP for registration for {}: {}", maskPhoneNumber(formattedPhoneNumber), otp);

        otpService.saveTempRegisterRequest(registerRequest);

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "OTP has been sent to your phone number", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        String formattedPhoneNumber = otpService.formatPhoneNumber(phoneNumber);

        RegisterRequest tempRegisterRequest = otpService.getTempRegisterRequest(formattedPhoneNumber);

        if (tempRegisterRequest == null) {
            logger.warn("No temporary registration data found for {}", formattedPhoneNumber);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Temporary registration data not found", null));
        }

        boolean isValid = otpService.validateOtpForPurpose(formattedPhoneNumber, otp, "REGISTER");
        if (isValid) {
            ApiResponse<?> response = customerService.registerCustomer(tempRegisterRequest);
            if (response.getCode() == HttpStatus.OK.value()) {
                return ResponseEntity.ok(
                        new ApiResponse<>(HttpStatus.OK.value(), "Registration completed successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Registration failed", null));
            }
        } else {
            logger.warn("Invalid OTP for phone number {}", formattedPhoneNumber);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid OTP. Please try again.", null));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestParam String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Phone number is required", null));
        }

        String formattedPhoneNumber = otpService.formatPhoneNumber(phoneNumber);

        if (!customersRepository.existsByPhoneNumber(phoneNumber)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Phone number does not exist", null));
        }

        String otp = otpService.generateOtpForPurpose(formattedPhoneNumber, "FORGOT_PASSWORD");
        otpService.sendOtp(formattedPhoneNumber, otp);

        logger.info("Generated OTP for forgot-password for {}: {}", maskPhoneNumber(phoneNumber), otp);
        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "OTP has been sent to your phone number", null));
    }

    @PostMapping("/verify-otp-for-reset-password")
    public ResponseEntity<ApiResponse<String>> verifyOtpForResetPassword(
            @RequestParam String phoneNumber, @RequestParam String otp) {

        String formattedPhoneNumber = otpService.formatPhoneNumber(phoneNumber);

        boolean isOtpValid = otpService.validateOtpForPurpose(formattedPhoneNumber, otp, "FORGOT_PASSWORD");
        if (!isOtpValid) {
            logger.warn("Invalid OTP for forgot-password for phone number {}", formattedPhoneNumber);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid OTP. Please try again.", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "OTP validated successfully. Proceed to reset password.", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPasswordAfterOtpVerification(
            @RequestParam String phoneNumber,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        String formattedPhoneNumber = otpService.formatPhoneNumber(phoneNumber);

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Passwords do not match", null));
        }

        try {
            ApiResponse<String> response = customerService.updatePassword(formattedPhoneNumber, newPassword);
            if (response.getCode() == HttpStatus.OK.value()) {
                return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Password reset successful", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Password reset failed", null));
            }
        } catch (Exception e) {
            logger.error("Error while resetting password for phone number {}: {}", formattedPhoneNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while resetting password", null));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateCustomer(
            @RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        ApiResponse<String> response = authService.authenticateUser(loginRequest);

        if (response.getCode() == HttpStatus.OK.value()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/google")
    public ResponseEntity<ApiResponse<String>> googleLogin(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        Customers customer = customerService.findByEmailAndAuthProvider(email, AuthProvider.GOOGLE)
                .orElseGet(() -> {
                    Customers newCustomer = new Customers();
                    newCustomer.setEmail(email);
                    newCustomer.setFullName(fullName);
                    newCustomer.setProviderId(providerId);
                    newCustomer.setRole(Role.CUSTOMER);
                    newCustomer.setAuthProvider(AuthProvider.GOOGLE);
                    return customerService.save(newCustomer);
                });

        String token = jwtUtil.generateTokenOAuth2(customer);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Google login successful", token));
    }

    @GetMapping("/facebook")
    public ResponseEntity<ApiResponse<String>> facebookLogin(@AuthenticationPrincipal OAuth2User oAuth2User) {
        Customers customer = new Customers();
        customer.setEmail(oAuth2User.getAttribute("email"));
        customer.setFullName(oAuth2User.getAttribute("name"));
        customer.setProviderId(oAuth2User.getAttribute("id"));
        customer.setRole(Role.CUSTOMER);
        customer.setAuthProvider(AuthProvider.FACEBOOK);

        String token = jwtUtil.generateTokenOAuth2(customer);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Facebook login successful", token));
    }

    private String maskPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll(".(?=.{4})", "*");
    }
}
