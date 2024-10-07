package com.koi_express.config;

import com.koi_express.entity.account.SystemAccount;
import com.koi_express.enums.Role;
import com.koi_express.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DefaultAccountConfig {

    private final SystemAccountRepository systemAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeDefaultManagerAccount() {
        return args -> {
            String email = "manager@koi-express.com";
            if (!systemAccountRepository.existsByEmail(email)) {
                SystemAccount managerAccount = SystemAccount.builder()
                        .accountId(0L) // Setting the ID to 0 as per your requirement
                        .fullName("Koi Express")
                        .email(email)
                        .phoneNumber("0000000000")
                        .passwordHash(passwordEncoder.encode("manager123"))
                        .role(Role.MANAGER) // Assuming Role.MANAGER is an appropriate role
                        .active(true)
                        .build();
                systemAccountRepository.save(managerAccount);
            }
        };
    }
}
