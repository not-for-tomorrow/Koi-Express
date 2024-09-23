package com.koi_express.controller;

import com.koi_express.dto.request.UpdateRequest;
import com.koi_express.entity.Customers;
import com.koi_express.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Customers>> getAllCustomers() {
        List<Customers> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

//    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        boolean isDeleted = customerService.delteteCustomer(id);
        if (isDeleted) {
            return ResponseEntity.ok("Customer deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Customer not found");
        }
    }

    @GetMapping("{customerId}")
//    @PreAuthorize("hasRole('MANAGER') or hasRole('CUSTOMER')")
    public ResponseEntity<Customers> getCustomerById(@PathVariable Long customerId) {
        Customers customers = customerService.getCustomerById(customerId);
        if( customers != null ) {
            return ResponseEntity.ok(customers);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
