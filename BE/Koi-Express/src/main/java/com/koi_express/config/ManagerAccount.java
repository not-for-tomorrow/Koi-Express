package com.koi_express.config;

import com.koi_express.entity.Customers;
import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import com.koi_express.repository.CustomersRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ManagerAccount {

    private final CustomersRepository customersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ManagerAccount(CustomersRepository customersRepository, PasswordEncoder passwordEncoder) {
        this.customersRepository = customersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        String managerPhone = "0981667547";
        String managerEmail = "manager@koi-express.com";

        if(!customersRepository.existsByEmail(managerEmail)) {
            Customers manager = Customers.builder()
                    .phoneNumber(managerPhone)
                    .email(managerEmail)
                    .passwordHash(passwordEncoder.encode("manager123"))
                    .authProvider(AuthProvider.LOCAL)
                    .role(Role.MANAGER)
                    .createdAt(LocalDateTime.now())
                    .build();

            customersRepository.save(manager);

            System.out.println("Manager account created successfully!");
        } else {
            System.out.println("Manager account already exists!");
        }
    }
}
