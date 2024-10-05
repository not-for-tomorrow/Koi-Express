package com.koi_express.controller;

import com.koi_express.dto.request.LoginRequest;
import com.koi_express.dto.request.RegisterRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.service.Customer.CustomerService;
import com.koi_express.service.Verification.OtpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerService customerService;
    private final OtpService otpService;

    @Autowired
    public AuthController(CustomerService customerService, OtpService otpService) {
        this.customerService = customerService;
        this.otpService = otpService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerCustomer(@RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        // Keep the original number for logging purposes
        String originalPhoneNumber = registerRequest.getPhoneNumber();

        // Format the phone number for internal usage (e.g., storing, sending OTP)
        String formattedPhoneNumber = otpService.formatPhoneNumber(originalPhoneNumber);

        // Generate OTP only once, log the original number for OTP generation
        String otp = String.format("%04d", new SecureRandom().nextInt(10000)); // Generate OTP
        otpService.saveOtp(formattedPhoneNumber, otp); // Save the OTP internally for later validation

        // Log the OTP generation using the original phone number
        System.out.println("Generated OTP for " + originalPhoneNumber + ": " + otp);

        // Send the OTP using the formatted phone number
        otpService.sendOtp(formattedPhoneNumber, otp);

        // Save temporary registration data with the formatted phone number
        registerRequest.setPhoneNumber(originalPhoneNumber);
        otpService.saveTempRegisterRequest(registerRequest);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "OTP has been sent to your phone number", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        // No formatting, use original phone number for retrieval
        RegisterRequest tempRegisterRequest = otpService.getTempRegisterRequest(phoneNumber);

        if (tempRegisterRequest != null) {
            // Validate the OTP
            boolean isValid = otpService.validateOtp(otpService.formatPhoneNumber(phoneNumber), otp);
            if (isValid) {
                // Create customer account and save it in the database
                ApiResponse<?> response = customerService.registerCustomer(tempRegisterRequest);
                if (response.getCode() == HttpStatus.OK.value()) {
                    return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Registration completed successfully", null));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Registration failed", null));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid OTP. Please try again.", null));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Temporary registration data not found", null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateCustomer(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        ApiResponse<String> response = customerService.authenticateCustomer(loginRequest);

        if(response.getCode() == HttpStatus.OK.value()){
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/google")
    public ResponseEntity<ApiResponse<String>> googleLogin(@AuthenticationPrincipal OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null || name == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "OAuth2 login failed", null));
        }
        
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Google login successful", email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/facebook")
    public ResponseEntity<ApiResponse<String>> facebookLogin(@AuthenticationPrincipal OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null || name == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "OAuth2 login failed", null));
        }

        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Facebook login successful", email);
        return ResponseEntity.ok(response);
    }

}
