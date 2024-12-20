package com.koi_express.controller.customer;

import com.koi_express.dto.request.UpdateRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.dto.response.BasicInfoResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.service.customer.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Customers>> updateCustomer(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid UpdateRequest updateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Validation failed";
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        String jwt = token.substring(7);
        String customerId = jwtUtil.extractCustomerId(jwt);

        logger.info("Extracted customerId: {}", customerId);

        try {
            Long parsedCustomerId = Long.parseLong(customerId);
            ApiResponse<Customers> response = customerService.updateCustomer(parsedCustomerId, updateRequest);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid customerId", null));
        }
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
