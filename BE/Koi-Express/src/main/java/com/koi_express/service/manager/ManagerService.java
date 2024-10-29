package com.koi_express.service.manager;

import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.DeliveringStaffLevel;
import com.koi_express.enums.Role;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.DeliveringStaffRepository;
import com.koi_express.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    private final ManageCustomerService manageCustomerService;
    private final SystemAccount systemAccountService;
    private final OrderRepository orderRepository;
    private final DeliveringStaffRepository deliveringStaffRepository;
    private final DeliveringStaffAccount deliveringStaffAccount;

    public Customers findByPhoneNumber(String phoneNumber) {
        return manageCustomerService.findByPhoneNumber(phoneNumber);
    }

    public void deleteCustomer(Long id) {
        manageCustomerService.deleteCustomer(id);
    }

    public Customers getCustomerById(Long customerId) {
        return manageCustomerService.getCustomerById(customerId);
    }

    public void updateCustomer(Long id, String fullName, String address) {
        manageCustomerService.updateCustomer(id, fullName, address);
    }

    public ApiResponse<String> createSalesStaffAccount(CreateStaffRequest createStaffRequest) {

        createStaffRequest.setRole(Role.SALES_STAFF);

        if (createStaffRequest.getRole() != Role.SALES_STAFF) {
            throw new AppException(
                    ErrorCode.INVALID_ROLE, "Invalid role for sales staff: " + createStaffRequest.getRole());
        }

        return systemAccountService.createSalesStaffAccount(createStaffRequest);
    }

    public ApiResponse<String> createDeliveringStaffAccount(CreateStaffRequest createStaffRequest) {

        logger.info("Role being set for new Delivering Staff: {}", createStaffRequest.getRole());

        createStaffRequest.setRole(Role.DELIVERING_STAFF);

        return deliveringStaffAccount.createDeliveringStaffAccount(createStaffRequest);
    }

    public List<com.koi_express.entity.account.SystemAccount> getAllSalesStaffAccounts() {
        return systemAccountService.getAllAccountsByRole(Role.SALES_STAFF);
    }

    public List<com.koi_express.entity.shipment.DeliveringStaff> getAllDeliveringStaffAccounts() {
        return deliveringStaffAccount.getAllAccountsByRole(Role.DELIVERING_STAFF);
    }

    public void autoUpdateDeliveringStaffLevel(Long staffId) {
        DeliveringStaff staff = deliveringStaffRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, "Delivering staff not found"));

        staff.updateLevel(); // Calls the automatic level update logic
        deliveringStaffRepository.save(staff); // Persist the updated level
        logger.info("Auto-updated level for Delivering Staff ID {}: {}", staffId, staff.getLevel());
    }

    public ApiResponse<String> promoteDeliveringStaff(Long staffId, DeliveringStaffLevel targetLevel) {
        DeliveringStaff staff = deliveringStaffRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, "Delivering staff not found"));

        if (staff.getLevel().compareTo(targetLevel) >= 0) {
            throw new AppException(ErrorCode.INVALID_LEVEL, "Cannot promote to the same or lower level");
        }

        staff.setLevel(targetLevel);
        deliveringStaffRepository.save(staff);
        logger.info("Manually promoted Delivering Staff ID {} to level {}", staffId, targetLevel);

        return new ApiResponse<>(200, "Delivering staff promoted successfully", null);
    }

    public BigDecimal calculateDailyRevenue(LocalDate date) {
        return orderRepository.findTotalRevenueByDate(date).orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateWeeklyRevenue(int year, int week) {
        return orderRepository.findTotalRevenueByWeek(year, week).orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateMonthlyRevenue(YearMonth month) {
        return orderRepository.findTotalRevenueByMonth(month.getYear(), month.getMonthValue()).orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateYearlyRevenue(int year) {
        return orderRepository.findTotalRevenueByYear(year).orElse(BigDecimal.ZERO);
    }

    // Top Customer by Order Frequency
    public Customers findTopCustomer() {
        return orderRepository.findCustomerWithMostOrders().orElse(null);
    }

    // Growth Calculations
    public BigDecimal calculateWeeklyGrowth(int year, int currentWeek) {
        BigDecimal currentWeekRevenue = calculateWeeklyRevenue(year, currentWeek);
        // Calculate the previous week, considering the transition between years
        int previousYear = (currentWeek == 1) ? year - 1 : year;
        int previousWeek = (currentWeek == 1) ? 52 : currentWeek - 1; // Assuming 52 weeks per year
        BigDecimal previousWeekRevenue = calculateWeeklyRevenue(previousYear, previousWeek);
        return calculateGrowth(currentWeekRevenue, previousWeekRevenue);
    }

    public BigDecimal calculateMonthlyGrowth(YearMonth currentMonth) {
        YearMonth previousMonth = currentMonth.minusMonths(1); // Automatically handles year transition
        BigDecimal currentMonthRevenue = calculateMonthlyRevenue(currentMonth);
        BigDecimal previousMonthRevenue = calculateMonthlyRevenue(previousMonth);
        return calculateGrowth(currentMonthRevenue, previousMonthRevenue);
    }

    public BigDecimal calculateYearlyGrowth(int currentYear) {
        int previousYear = currentYear - 1;
        BigDecimal currentYearRevenue = calculateYearlyRevenue(currentYear);
        BigDecimal previousYearRevenue = calculateYearlyRevenue(previousYear);
        return calculateGrowth(currentYearRevenue, previousYearRevenue);
    }

    private BigDecimal calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return (current.subtract(previous)).divide(previous, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    // Highest Revenue Calculations
    public LocalDate getHighestRevenueDay() {
        return orderRepository.findHighestRevenueDay().orElse(null);
    }

    public YearMonth getHighestRevenueMonth() {
        return orderRepository.findHighestRevenueMonth().orElse(null);
    }

    public int getHighestRevenueYear() {
        return orderRepository.findHighestRevenueYear().orElse(0);
    }
}
