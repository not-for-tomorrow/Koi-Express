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

//            ApiResponse<?> response = customerService.registerCustomer(registerRequest);

        String formattedPhoneNumber = otpService.formatPhoneNumber(registerRequest.getPhoneNumber());
        otpService.sendOtp(formattedPhoneNumber);

        // Lưu thông tin đăng ký tạm thời để xác minh sau
        registerRequest.setPhoneNumber(formattedPhoneNumber);
        otpService.saveTempRegisterRequest(registerRequest);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "OTP has been sent to your phone number", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        String formattedPhoneNumber = otpService.formatPhoneNumber(phoneNumber);

        boolean isValid = otpService.validateOtp(formattedPhoneNumber, otp);
        if (isValid) {
            // Tìm thông tin đăng ký tạm thời dựa vào số điện thoại
            RegisterRequest tempRegisterRequest = otpService.getTempRegisterRequest(formattedPhoneNumber);
            if (tempRegisterRequest != null) {
                // Tạo tài khoản cho khách hàng và lưu vào cơ sở dữ liệu
                ApiResponse<?> response = customerService.registerCustomer(tempRegisterRequest);
                if (response.getCode() == HttpStatus.OK.value()) {
                    return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Registration completed successfully", null));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Registration failed", null));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Temporary registration data not found", null));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid OTP. Please try again.", null));
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
