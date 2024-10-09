package com.koi_express.controller;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.UpdateRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.dto.response.BasicInfoResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.service.customer.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    @Autowired
    public CustomerController(
            CustomerService customerService, JwtUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Customers>> updateCustomer(
            @PathVariable Long id, @RequestBody @Valid UpdateRequest updateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Validation failed";
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        ApiResponse<Customers> response = customerService.updateCustomer(id, updateRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("me")
    public ResponseEntity<ApiResponse<Customers>> getCustomerDetails(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String phoneNumber = jwtUtil.extractPhoneNumber(jwt);

        Customers customers = customerService.getCustomerDetails(phoneNumber);
        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.OK.value(), "User details retrieved successfully", customers),
                HttpStatus.OK);
    }

    @GetMapping("/basic-info")
    public ResponseEntity<ApiResponse<BasicInfoResponse>> getCustomerBasicInfo(
            @RequestHeader("Authorization") String token) {

        String jwt = token.substring(7);

        String phoneNumber = jwtUtil.extractPhoneNumber(jwt);

        Customers customer = customerService.getCustomerDetails(phoneNumber);

        BasicInfoResponse basicInfo =
                new BasicInfoResponse(customer.getFullName(), customer.getPhoneNumber(), customer.getEmail());

        return new ResponseEntity<>(
                new ApiResponse<>(
                        HttpStatus.OK.value(), "Customer basic information retrieved successfully", basicInfo),
                HttpStatus.OK);
    }
}
