package com.koi_express.controller;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.service.Manager.ManagerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    private final ManagerService managerService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ManagerController(ManagerService managerService, JwtUtil jwtUtil) {
        this.managerService = managerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create-staff")
    public ResponseEntity<ApiResponse<?>> createsStaff(@RequestBody @Valid CreateStaffRequest createStaffRequest, HttpServletRequest httpServletRequest) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        ApiResponse<?> response = managerService.createStaffAccount(createStaffRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Page<Customers>> getAllCustomers(
            HttpServletRequest httpServletRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        Pageable paging = PageRequest.of(page, size);
        Page<Customers> customersPage  = managerService.getAllCustomers(paging, token);

        return new ResponseEntity<>(customersPage, HttpStatus.OK);
    }

    @GetMapping("/id/{customerId}")
    public ResponseEntity<ApiResponse<Customers>> getCustomerById(
            HttpServletRequest httpServletRequest,
            @PathVariable Long customerId) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        Customers customers = managerService.getCustomerById(customerId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "Customer find", customers), HttpStatus.OK);
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Customers> getCustomerByPhoneNumber(
            HttpServletRequest httpServletRequest,
            @PathVariable String phoneNumber) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        Customers customers = managerService.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(customers);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(
            HttpServletRequest httpServletRequest,
            @PathVariable Long id) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        managerService.deleteCustomer(id);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "Customer deleted successfully.", null), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateCustomer(
            HttpServletRequest httpServletRequest,
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String address) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        managerService.updateCustomer(id, fullName, address);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "Customer updated successfully.", null), HttpStatus.OK);
    }

}
