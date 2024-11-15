package com.koi_express.controller.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.request.CustomerTopSpenderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.account.SystemAccount;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.DeliveringStaffLevel;
import com.koi_express.service.customer.CustomerService;
import com.koi_express.service.manager.ManageCustomerService;
import com.koi_express.service.manager.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerController {

    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    private final ManagerService managerService;
    private final ManageCustomerService manageCustomerService;
    private final CustomerService customerService;

    @PostMapping("/create-sales-staff")
    public ResponseEntity<ApiResponse<String>> createSalesStaff(
            @RequestBody @Valid CreateStaffRequest createStaffRequest) {

        ApiResponse<String> response = managerService.createSalesStaffAccount(createStaffRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/create-delivering-staff")
    public ResponseEntity<ApiResponse<String>> createDeliveringStaff(
            @RequestBody @Valid CreateStaffRequest createDeliveringStaffRequest) {

        ApiResponse<String> response = managerService.createDeliveringStaffAccount(createDeliveringStaffRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<Customers>>> getAllCustomers() {
        ApiResponse<List<Customers>> customersResponse = manageCustomerService.getAllCustomers();
        return ResponseEntity.ok(customersResponse);
    }

    @GetMapping("/id/{customerId}")
    public ResponseEntity<ApiResponse<Customers>> getCustomerById(@PathVariable Long customerId) {

        Customers customer = managerService.getCustomerById(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer found", customer));
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<Customers>> getCustomerByPhoneNumber(@PathVariable String phoneNumber) {

        Customers customer = managerService.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success("Customer found by phone number", customer));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(@PathVariable Long id) {

        managerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully.", null));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateCustomer(
            @PathVariable Long id, @RequestParam String fullName, @RequestParam String address) {

        managerService.updateCustomer(id, fullName, address);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully.", null));
    }

    @GetMapping("/sales-staff")
    public ResponseEntity<ApiResponse<List<SystemAccount>>> getAllSalesStaff() {
        List<SystemAccount> salesStaffAccounts = managerService.getAllSalesStaffAccounts();
        return ResponseEntity.ok(ApiResponse.success("Sales staff retrieved successfully.", salesStaffAccounts));
    }

    @GetMapping("/delivering-staff")
    public ResponseEntity<ApiResponse<List<DeliveringStaff>>> getAllDeliveringStaff() {
        List<DeliveringStaff> deliveringStaffAccounts = managerService.getAllDeliveringStaffAccounts();
        return ResponseEntity.ok(
                ApiResponse.success("Delivering staff retrieved successfully.", deliveringStaffAccounts));
    }

    @PutMapping("/delivering-staff/{staffId}/update-level")
    public ResponseEntity<ApiResponse<String>> updateDeliveringStaffLevel(
            @PathVariable Long staffId, @RequestParam DeliveringStaffLevel targetLevel) {

        ApiResponse<String> response = managerService.promoteDeliveringStaff(staffId, targetLevel);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/total-amount/daily")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalAmountPerDay() {
        try {
            LocalDateTime latestDate = LocalDateTime.now();

            BigDecimal totalAmount = managerService.calculateTotalAmountPerDay(latestDate);
            return ResponseEntity.ok(
                    ApiResponse.success("Total amount for " + latestDate + " retrieved successfully", totalAmount));
        } catch (Exception e) {
            logger.error("Error retrieving total amount for the latest date: ", e); // Log the exception
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Error retrieving total amount for the latest date"));
        }
    }

    @GetMapping("/total-amount")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalAmount() {
        try {
            BigDecimal totalAmount = managerService.calculateTotalAmount();
            return ResponseEntity.ok(ApiResponse.success("Total amount retrieved successfully", totalAmount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Error retrieving total amount"));
        }
    }

    @GetMapping("/number-of-customers")
    public ResponseEntity<ApiResponse<Integer>> getNumberOfCustomers() {
        try {
            int numberOfCustomers = managerService.getNumberOfCustomers();
            return ResponseEntity.ok(
                    ApiResponse.success("Number of customers retrieved successfully", numberOfCustomers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Error retrieving number of customers"));
        }
    }

    @GetMapping("/number-of-orders")
    public ResponseEntity<ApiResponse<Integer>> getNumberOfOrders() {
        try {
            int numberOfOrders = managerService.getNumberOfOrders();
            return ResponseEntity.ok(ApiResponse.success("Number of orders retrieved successfully", numberOfOrders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Error retrieving number of orders"));
        }
    }

    @GetMapping("/total-amount/yearly")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getTotalAmountPerMonth(@RequestParam int year) {
        try {
            Map<String, BigDecimal> monthlyTotals = managerService.calculateMonthlyAmountsForYear(year);
            return ResponseEntity.ok(
                    ApiResponse.success("Monthly totals for year " + year + " retrieved successfully", monthlyTotals));
        } catch (Exception e) {
            logger.error("Error retrieving monthly totals for year " + year, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Error retrieving monthly totals for year " + year));
        }
    }

    @GetMapping("/top-spenders")
    public List<CustomerTopSpenderRequest> getTop10CustomersBySpending() {
        return customerService.getTop10CustomersBySpending();
    }

    @PutMapping("/delivering-staff/{staffId}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateDeliveringStaff(@PathVariable Long staffId) {

        boolean deactivated = managerService.deactivateDeliveringStaff(staffId);

        if (deactivated) {
            return ResponseEntity.ok(ApiResponse.success("Delivering staff account deactivated successfully.", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Delivering staff account is already inactive."));
        }
    }

    @PutMapping("/sales-staff/{accountId}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateSalesStaff(@PathVariable Long accountId) {

        try {
            boolean deactivated = managerService.deactivateSalesStaff(accountId);

            if (deactivated) {
                return ResponseEntity.ok(ApiResponse.success("Sales staff account deactivated successfully.", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.badRequest("Sales staff account is already inactive."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }
}
