package com.koi_express.controller;

import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.account.SystemAccount;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.service.manager.ManageCustomerService;
import com.koi_express.service.manager.ManagerService;
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

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    private final ManagerService managerService;

    private final ManageCustomerService manageCustomerService;

    @Autowired
    public ManagerController(ManagerService managerService, ManageCustomerService manageCustomerService) {
        this.managerService = managerService;
        this.manageCustomerService = manageCustomerService;
    }

    @PostMapping("/create-sales-staff")
    public ResponseEntity<ApiResponse<?>> createSalesStaff(
            @RequestBody @Valid CreateStaffRequest createStaffRequest, HttpServletRequest httpServletRequest) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        ApiResponse<?> response = managerService.createSalesStaffAccount(createStaffRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/create-delivering-staff")
    public ResponseEntity<ApiResponse<?>> createDeliveringStaff(
            @RequestBody @Valid CreateStaffRequest createDeliveringStaffRequest,
            HttpServletRequest httpServletRequest) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        ApiResponse<?> response = managerService.createDeliveringStaffAccount(createDeliveringStaffRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping(value = "/customers")
    public ResponseEntity<List<Customers>> getAllCustomers(
            HttpServletRequest httpServletRequest) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        List<Customers> customersPage = manageCustomerService.getAllCustomers();

        return new ResponseEntity<>(customersPage, HttpStatus.OK);
    }

    @GetMapping("/id/{customerId}")
    public ResponseEntity<ApiResponse<Customers>> getCustomerById(
            HttpServletRequest httpServletRequest, @PathVariable Long customerId) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        Customers customers = managerService.getCustomerById(customerId);
        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.OK.value(), "Customer find", customers), HttpStatus.OK);
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Customers> getCustomerByPhoneNumber(
            HttpServletRequest httpServletRequest, @PathVariable String phoneNumber) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        Customers customers = managerService.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(customers);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(
            HttpServletRequest httpServletRequest, @PathVariable Long id) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        managerService.deleteCustomer(id);
        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.OK.value(), "Customer deleted successfully.", null), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateCustomer(
            HttpServletRequest httpServletRequest,
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String address) {

        String token = httpServletRequest.getHeader("Authorization").substring(7);

        managerService.updateCustomer(id, fullName, address);
        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.OK.value(), "Customer updated successfully.", null), HttpStatus.OK);
    }

    @GetMapping("/sales-staff")
    public ResponseEntity<List<SystemAccount>> getAllSalesStaff() {

        List<SystemAccount> salesStaffAccounts = managerService.getAllSalesStaffAccounts();
        return new ResponseEntity<>(salesStaffAccounts, HttpStatus.OK);
    }

    @GetMapping("/delivering-staff")
    public ResponseEntity<List<DeliveringStaff>> getAllDeliveringStaff() {

        List<DeliveringStaff> deliveringStaffAccounts = managerService.getAllDeliveringStaffAccounts();
        return new ResponseEntity<>(deliveringStaffAccounts, HttpStatus.OK);
    }
}
