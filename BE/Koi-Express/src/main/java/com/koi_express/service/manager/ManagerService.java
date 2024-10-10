package com.koi_express.service.manager;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.account.SystemAccount;
import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.Role;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    private ManageCustomerService manageCustomerService;

    @Autowired
    private SystemAccountService systemAccountService;

    @Autowired
    private DeliveringStaffService deliveringStaffService;

    @Autowired
    private JwtUtil jwtUtil;

    public Customers findByPhoneNumber(String phoneNumber) {
        return manageCustomerService.findByPhoneNumber(phoneNumber);
    }

    public boolean deleteCustomer(Long id) {
        return manageCustomerService.deleteCustomer(id);
    }

    public Customers getCustomerById(Long customerId) {
        return manageCustomerService.getCustomerById(customerId);
    }

    public Customers updateCustomer(Long id, String fullName, String address) {
        return manageCustomerService.updateCustomer(id, fullName, address);
    }

    public ApiResponse<?> createSalesStaffAccount(CreateStaffRequest createStaffRequest) {

        createStaffRequest.setRole(Role.SALES_STAFF);

        if (createStaffRequest.getRole() != Role.SALES_STAFF) {
            throw new AppException(
                    ErrorCode.INVALID_ROLE, "Invalid role for sales staff: " + createStaffRequest.getRole());
        }
        return systemAccountService.createSalesStaffAccount(createStaffRequest);
    }

    // Separate method for creating delivering staff account
    public ApiResponse<?> createDeliveringStaffAccount(CreateStaffRequest createStaffRequest) {

        logger.info("Role being set for new Delivering Staff: {}", createStaffRequest.getRole());

        createStaffRequest.setRole(Role.DELIVERING_STAFF);

        //        if (createStaffRequest.getRole() != Role.DELIVERING_STAFF) {
        //            throw new AppException(ErrorCode.INVALID_ROLE, "Invalid role for delivering staff: " +
        // createStaffRequest.getRole());
        //        }
        return deliveringStaffService.createDeliveringStaffAccount(createStaffRequest);
    }

    public Page<SystemAccount> getAllSalesStaffAccounts(Pageable pageable) {
        return systemAccountService.getAllAccountsByRole(Role.SALES_STAFF, pageable);
    }

    public Page<SystemAccount> getAllDeliveringStaffAccounts(Pageable pageable) {
        return systemAccountService.getAllAccountsByRole(Role.DELIVERING_STAFF, pageable);
    }
}
