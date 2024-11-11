package com.koi_express.service.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.enums.DeliveringStaffLevel;
import com.koi_express.enums.Role;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.DeliveringStaffRepository;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    private final ManageCustomerService manageCustomerService;
    private final SystemAccount systemAccountService;
    private final OrderRepository orderRepository;
    private final DeliveringStaffRepository deliveringStaffRepository;
    private final DeliveringStaffAccount deliveringStaffAccount;
    private final SystemAccountRepository systemAccountRepository;
    private final CustomersRepository customerRepository;

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

    public ApiResponse<String> promoteDeliveringStaff(Long staffId, DeliveringStaffLevel targetLevel) {
        DeliveringStaff staff = deliveringStaffRepository
                .findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND, "Delivering staff not found"));

        if (staff.getLevel().compareTo(targetLevel) >= 0) {
            throw new AppException(ErrorCode.INVALID_LEVEL, "Cannot promote to the same or lower level");
        }

        staff.setLevel(targetLevel);
        deliveringStaffRepository.save(staff);
        logger.info("Manually promoted Delivering Staff ID {} to level {}", staffId, targetLevel);

        return new ApiResponse<>(200, "Delivering staff promoted successfully", null);
    }

    public boolean deactivateDeliveringStaff(Long staffId) {
        DeliveringStaff deliveringStaff = deliveringStaffRepository
                .findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with id: " + staffId));

        if (!deliveringStaff.isActive()) {
            return false;
        }

        deliveringStaff.setActive(false);
        deliveringStaffRepository.save(deliveringStaff);

        return true;
    }

    public boolean deactivateSalesStaff(Long accountId) {
        com.koi_express.entity.account.SystemAccount account = systemAccountRepository
                .findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        if (account.getRole() != Role.SALES_STAFF) {
            throw new IllegalArgumentException("Only SALES_STAFF accounts can be deactivated via this method.");
        }

        if (!account.isActive()) {
            return false;
        }

        account.setActive(false);
        systemAccountRepository.save(account);

        return true;
    }

    public BigDecimal calculateTotalAmountPerDay(LocalDateTime date) {
        return orderRepository.findTotalAmountByDate(date).orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateTotalAmount() {
        return orderRepository.findTotalAmount().orElse(BigDecimal.ZERO);
    }

    public int getNumberOfCustomers() {
        return (int) customerRepository.count();
    }

    public int getNumberOfOrders() {
        return (int) orderRepository.count();
    }

    public Map<String, BigDecimal> calculateMonthlyAmountsForYear(int year) {
        Map<String, BigDecimal> monthlyTotals = new LinkedHashMap<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);

            BigDecimal totalForMonth =
                    orderRepository.findTotalAmountByMonthAndYear(year, month).orElse(BigDecimal.ZERO);

            monthlyTotals.put(yearMonth.toString(), totalForMonth);
        }
        return monthlyTotals;
    }
}
