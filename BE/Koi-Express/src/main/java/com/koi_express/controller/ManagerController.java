package com.koi_express.controller;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.Customers;
import com.koi_express.service.ManagerService;
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
public class ManagerController {

    private final ManagerService managerService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ManagerController(ManagerService managerService, JwtUtil jwtUtil) {
        this.managerService = managerService;
        this.jwtUtil = jwtUtil;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<Page<Customers>> getAllCustomers(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<Customers> customersPage  = managerService.getAllCustomers(paging);

        return new ResponseEntity<>(customersPage, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/id/{customerId}")
    public ResponseEntity<ApiResponse<Customers>> getCustomerById(@RequestHeader("Authorization") String token, @PathVariable Long customerId) {

        String jwt = token.substring(7);
        String role = jwtUtil.extractClaim(jwt, claims -> claims.get("role", String.class));

        if (!"MANAGER".equals(role)) {
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Access denied. Manager role required.", null), HttpStatus.FORBIDDEN);
        }

        Customers customers = managerService.getCustomerById(customerId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "Customer find", customers), HttpStatus.OK);
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Customers> getCustomerByPhoneNumber(@PathVariable String phoneNumber) {
        Customers customers = managerService.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(customers);
    }

    //    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        boolean isDeleted = managerService.deleteCustomer(id);
        if (isDeleted) {
            return ResponseEntity.ok("Customer deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Customer not found");
        }
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Customers> updateCustomer(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String address) {
        Customers updatedCustomer = managerService.updateCustomer(id, fullName, address);
        return ResponseEntity.ok(updatedCustomer);
    }

}
