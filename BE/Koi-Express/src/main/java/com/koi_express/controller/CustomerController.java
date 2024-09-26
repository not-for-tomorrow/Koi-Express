package com.koi_express.controller;

import com.koi_express.dto.request.UpdateRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.Customers;
import com.koi_express.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Customers>> updateCustomer(
            @PathVariable Long id,
            @RequestBody @Valid UpdateRequest updateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError()!=null? bindingResult.getFieldError().getDefaultMessage() : "Validation failed";
            return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
        }

        ApiResponse<Customers> response = customerService.updateCustomer(id, updateRequest);
        return ResponseEntity.ok(response);
    }
}

