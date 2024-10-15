package com.koi_express.config;

import com.koi_express.entity.account.SystemAccount;
import com.koi_express.enums.Role;
import com.koi_express.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DefaultAccountConfig {

    private final SystemAccountRepository systemAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.manager.email}")
    private String managerEmail;

    @Value("${spring.manager.password}")
    private String managerPassword;

    @Bean
    public CommandLineRunner initializeDefaultManagerAccount() {
        return args -> {
            if (!systemAccountRepository.existsByEmail(managerEmail)) {
                SystemAccount managerAccount = SystemAccount.builder()
                        .accountId(0L)
                        .fullName("Koi Express")
                        .email(managerEmail)
                        .phoneNumber("0000000000")
                        .passwordHash(passwordEncoder.encode(managerPassword))
                        .role(Role.MANAGER)
                        .active(true)
                        .build();
                systemAccountRepository.save(managerAccount);
            }
        };
    }
}
