package com.koi_express.controller;

import com.koi_express.dto.request.LoginRequest;
import com.koi_express.dto.request.RegisterRequest;
import com.koi_express.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterRequest registerRequest,  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Trả về lỗi xác thực chi tiết
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }

        String result = customerService.registerCustomer(registerRequest);
        if (result.equals("User registered successfully!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Trả về lỗi xác thực chi tiết
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        }

        String result = customerService.authenticateCustomer(loginRequest);
        if (result.equals("Login successful!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(401).body(result);
        }
    }

//    @DeleteMapping("/delete/{customerId}")
//    public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
//        String result = customerService.delteteCustomer(customerId);
//        if(result.equals("Customer deleted successfully")) {
//            return ResponseEntity.ok(result);
//        } else {
//            return ResponseEntity.badRequest().body(result);
//        }
//    }
}
