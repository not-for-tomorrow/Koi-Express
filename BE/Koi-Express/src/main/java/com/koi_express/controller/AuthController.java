package com.koi_express.controller;

import java.security.SecureRandom;
import java.util.stream.Collectors;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.LoginRequest;
import com.koi_express.dto.request.RegisterRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import com.koi_express.service.customer.CustomerService;
import com.koi_express.service.verification.AuthService;
import com.koi_express.service.verification.OtpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerService customerService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    public AuthController(
            CustomerService customerService, OtpService otpService, JwtUtil jwtUtil, AuthService authService) {
        this.customerService = customerService;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerCustomer(
            @RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        String originalPhoneNumber = registerRequest.getPhoneNumber();

        String formattedPhoneNumber = otpService.formatPhoneNumber(originalPhoneNumber);

        String otp = String.format("%04d", new SecureRandom().nextInt(10000));
        otpService.saveOtp(formattedPhoneNumber, otp);

        System.out.println("Generated OTP for " + originalPhoneNumber + ": " + otp);

        otpService.sendOtp(formattedPhoneNumber, otp);

        registerRequest.setPhoneNumber(originalPhoneNumber);
        otpService.saveTempRegisterRequest(registerRequest);

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "OTP has been sent to your phone number", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {

        RegisterRequest tempRegisterRequest = otpService.getTempRegisterRequest(phoneNumber);

        if (tempRegisterRequest != null) {

            boolean isValid = otpService.validateOtp(otpService.formatPhoneNumber(phoneNumber), otp);
            if (isValid) {

                ApiResponse<?> response = customerService.registerCustomer(tempRegisterRequest);
                if (response.getCode() == HttpStatus.OK.value()) {
                    return ResponseEntity.ok(
                            new ApiResponse<>(HttpStatus.OK.value(), "Registration completed successfully", null));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Registration failed", null));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                HttpStatus.BAD_REQUEST.value(), "Invalid OTP. Please try again.", null));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(), "Temporary registration data not found", null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateCustomer(
            @RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
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
        Customers customer = new Customers(); // Replace with actual user fetching logic
        customer.setEmail(oAuth2User.getAttribute("email"));
        customer.setFullName(oAuth2User.getAttribute("name"));
        customer.setProviderId(oAuth2User.getAttribute("sub"));
        customer.setRole(Role.CUSTOMER);
        customer.setAuthProvider(AuthProvider.GOOGLE);

        String token = jwtUtil.generateTokenOAuth2(customer);

        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Google login successful", token);
        return ResponseEntity.ok(response);
    }

    // Facebook OAuth2 Login Endpoint
    @GetMapping("/facebook")
    public ResponseEntity<ApiResponse<String>> facebookLogin(@AuthenticationPrincipal OAuth2User oAuth2User) {
        Customers customer = new Customers();
        customer.setEmail(oAuth2User.getAttribute("email"));
        customer.setFullName(oAuth2User.getAttribute("name"));
        customer.setProviderId(oAuth2User.getAttribute("id"));
        customer.setRole(Role.CUSTOMER);
        customer.setAuthProvider(AuthProvider.FACEBOOK);

        String token = jwtUtil.generateTokenOAuth2(customer);

        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Facebook login successful", token);
        return ResponseEntity.ok(response);
    }
}
