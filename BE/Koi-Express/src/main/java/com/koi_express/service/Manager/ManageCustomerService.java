package com.koi_express.service.Manager;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.entity.customer.Customers;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.ManagerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManageCustomerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(ManageCustomerService.class);

    public Page<Customers> getAllCustomers(Pageable pageable, String token) {

        String customerId = jwtUtil.extractCustomerId(token);
        logger.info("Extracted customerId: {}", customerId);

        return managerRepository.findAll(pageable);
    }

    public Customers findByPhoneNumber(String phoneNumber) {
        Optional<Customers> customerOptional = managerRepository.findByPhoneNumber(phoneNumber);
        return customerOptional.orElseThrow(()
                -> new RuntimeException("Couldn't find'"));
    }

    public boolean deleteCustomer(Long id) {

        if(!managerRepository.existsById(id)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_FOUND);
        }

        managerRepository.deleteById(id);
        return true;
    }

    public Customers getCustomerById(Long customerId) {

        return managerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    public Customers updateCustomer(Long id, String fullName, String address) {

        Customers customer = managerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        customer.setFullName(fullName);
        customer.setAddress(address);
        return managerRepository.save(customer);
    }
}
