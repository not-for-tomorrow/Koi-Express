package com.koi_express.controller.manager;

import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.account.SystemAccount;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.DeliveringStaffLevel;
import com.koi_express.service.manager.ManageCustomerService;
import com.koi_express.service.manager.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    private final ManageCustomerService manageCustomerService;

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

    @GetMapping( "/customers")
    public ResponseEntity<ApiResponse<List<Customers>>> getAllCustomers() {

        ApiResponse<List<Customers>> customersPage = manageCustomerService.getAllCustomers();

        return new ResponseEntity<>(customersPage, HttpStatus.OK);
    }

    @GetMapping("/id/{customerId}")
    public ResponseEntity<ApiResponse<Customers>> getCustomerById(
            @PathVariable Long customerId) {

        Customers customers = managerService.getCustomerById(customerId);
        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.OK.value(), "Customer find", customers), HttpStatus.OK);
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Customers> getCustomerByPhoneNumber(
            @PathVariable String phoneNumber) {

        Customers customers = managerService.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(customers);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(
            @PathVariable Long id) {

        managerService.deleteCustomer(id);
        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.OK.value(), "Customer deleted successfully.", null), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateCustomer(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String address) {

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

    @PutMapping("/delivering-staff/{staffId}/update-level")
    public ResponseEntity<ApiResponse<String>> updateDeliveringStaffLevel(
            @PathVariable Long staffId,
            @RequestParam DeliveringStaffLevel targetLevel) {

        ApiResponse<String> response = managerService.promoteDeliveringStaff(staffId, targetLevel);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/revenue/daily")
    public BigDecimal getDailyRevenue(@RequestParam LocalDate date) {
        return managerService.calculateDailyRevenue(date);
    }

    @GetMapping("/revenue/weekly")
    public BigDecimal getWeeklyRevenue(@RequestParam int year, @RequestParam int week) {
        return managerService.calculateWeeklyRevenue(year, week);
    }

    @GetMapping("/revenue/monthly")
    public BigDecimal getMonthlyRevenue(@RequestParam YearMonth month) {
        return managerService.calculateMonthlyRevenue(month);
    }

    @GetMapping("/revenue/yearly")
    public BigDecimal getYearlyRevenue(@RequestParam int year) {
        return managerService.calculateYearlyRevenue(year);
    }

    // Top Customer by Order Frequency
    @GetMapping("/top-customer")
    public Customers getTopCustomer() {
        return managerService.findTopCustomer();
    }

    // Growth Comparison
    @GetMapping("/growth/weekly")
    public BigDecimal getWeeklyGrowth(@RequestParam int year, @RequestParam int currentWeek) {
        return managerService.calculateWeeklyGrowth(year, currentWeek);
    }

    @GetMapping("/growth/monthly")
    public BigDecimal getMonthlyGrowth(@RequestParam YearMonth currentMonth) {
        return managerService.calculateMonthlyGrowth(currentMonth);
    }

    @GetMapping("/growth/yearly")
    public BigDecimal getYearlyGrowth(@RequestParam int currentYear) {
        return managerService.calculateYearlyGrowth(currentYear);
    }

    // Highest Revenue by Day, Week, Month, Year
    @GetMapping("/highest-revenue/day")
    public LocalDate getHighestRevenueDay() {
        return managerService.getHighestRevenueDay();
    }

    @GetMapping("/highest-revenue/month")
    public YearMonth getHighestRevenueMonth() {
        return managerService.getHighestRevenueMonth();
    }

    @GetMapping("/highest-revenue/year")
    public int getHighestRevenueYear() {
        return managerService.getHighestRevenueYear();
    }
}
