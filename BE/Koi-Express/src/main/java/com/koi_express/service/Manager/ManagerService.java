package com.koi_express.service.Manager;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.CreateStaffRequest;
import com.koi_express.dto.response.ApiResponse;
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

    public Page<Customers> getAllCustomers(Pageable pageable, String token) {
        String customerId = jwtUtil.extractCustomerId(token);
        return manageCustomerService.getAllCustomers(pageable, customerId);
    }

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

    public ApiResponse<?> createStaffAccount(CreateStaffRequest createStaffRequest) {
        if (createStaffRequest.getRole() == Role.SALES_STAFF) {
            return systemAccountService.createSalesStaffAccount(createStaffRequest);
        } else if (createStaffRequest.getRole() == Role.DELIVERING_STAFF) {
            return deliveringStaffService.createDeliveringStaffAccount(createStaffRequest);
        } else {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }
    }
}
