package com.koi_express.controller;

import com.koi_express.dto.request.LoginRequest;
import com.koi_express.dto.request.RegisterRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.Customers;
import com.koi_express.service.CustomerService;
import com.koi_express.service.OtpService;
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

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Customers>> registerUser(@RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getDefaultMessage() : "Validation failed";
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        String generatedOtp = otpService.generateOtp();
        otpService.sendOtp(registerRequest.getPhoneNumber(), generatedOtp);

        customerService.saveOtp(registerRequest.getPhoneNumber(), generatedOtp);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "OTP sent successfully", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getDefaultMessage() : "Validation failed";
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        boolean isOtpValid = customerService.verifyOtp(registerRequest.getPhoneNumber(), registerRequest.getOtp());

        if(!isOtpValid){
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid OTP", null));
        }

        ApiResponse<Customers> response = customerService.registerCustomer(registerRequest);

        if(response.getCode() == HttpStatus.OK.value()){
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "User registered successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), response.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateUser(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null ? bindingResult.getFieldError().getDefaultMessage() : "Validation failed";
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

        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Facebook login successful", email);
        return ResponseEntity.ok(response);
    }

}
