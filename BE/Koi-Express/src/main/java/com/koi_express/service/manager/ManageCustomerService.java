package com.koi_express.service.manager;

import java.util.List;
import java.util.Optional;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageCustomerService {

    private final ManagerRepository managerRepository;

    public ApiResponse<List<Customers>> getAllCustomers() {

        return new ApiResponse<>(HttpStatus.OK.value(), "Customers fetched successfully.", managerRepository.findAll());
    }

    public Customers getCustomerById(Long customerId) {

        return managerRepository.findById(customerId).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    public void updateCustomer(Long id, String fullName, String address) {

        Customers customer =
                managerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        customer.setFullName(fullName);
        customer.setAddress(address);
        managerRepository.save(customer);
    }
}
