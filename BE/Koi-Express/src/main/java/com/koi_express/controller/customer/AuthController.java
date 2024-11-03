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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerCustomer(
            @RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            logger.warn("Validation errors during registration: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        String originalPhoneNumber = registerRequest.getPhoneNumber();
        String formattedPhoneNumber = otpService.formatPhoneNumber(originalPhoneNumber);
        String otp = String.format("%04d", new SecureRandom().nextInt(10000));

        otpService.saveOtp(formattedPhoneNumber, otp);

        logger.info("Generated OTP for {}: {}", originalPhoneNumber, otp);

        otpService.sendOtp(formattedPhoneNumber, otp);

        registerRequest.setPhoneNumber(originalPhoneNumber);
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
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(), "Temporary registration data not found", null));
        }

        boolean isValid = otpService.validateOtp(formattedPhoneNumber, otp);
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateCustomer(
            @RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            logger.warn("Validation errors during login: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        ApiResponse<String> response = authService.authenticateUser(loginRequest);
        return response.getCode() == HttpStatus.OK.value()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/google")
    public ResponseEntity<ApiResponse<String>> googleLogin(@AuthenticationPrincipal OAuth2User oAuth2User) {

        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        Customers customer = customerService
                .findByEmailAndAuthProvider(email, AuthProvider.GOOGLE)
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
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Google login successful", token);
        return ResponseEntity.ok(response);
    }

    // Facebook OAuth2 Login Endpoint
    @GetMapping("/facebook")
    public ResponseEntity<ApiResponse<String>> facebookLogin(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        Customers customer = customerService
                .findByEmailAndAuthProvider(email, AuthProvider.FACEBOOK)
                .orElseGet(() -> {
                    Customers newCustomer = new Customers();
                    newCustomer.setEmail(email);
                    newCustomer.setFullName(oAuth2User.getAttribute("name"));
                    newCustomer.setProviderId(oAuth2User.getAttribute("id"));
                    newCustomer.setRole(Role.CUSTOMER);
                    newCustomer.setAuthProvider(AuthProvider.FACEBOOK);
                    return customerService.save(newCustomer);
                });

        String token = jwtUtil.generateTokenOAuth2(customer);
        logger.info("Facebook login successful for user: {}", email);
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Facebook login successful", token);
        return ResponseEntity.ok(response);
    }
}
