package com.koi_express.service.customer;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.entity.customer.Customers;
import com.koi_express.enums.AuthProvider;
import com.koi_express.enums.Role;
import com.koi_express.repository.CustomersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private final CustomersRepository customersRepository;

    @Autowired
    private final JwtUtil jwtUtil;

    public CustomOAuth2UserService(CustomersRepository customersRepository, JwtUtil jwtUtil) {
        this.customersRepository = customersRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Get the registration ID (Google, Facebook, etc.)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // Get common attributes
        String email = oAuth2User.getAttribute("email");
        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        String fullName = oAuth2User.getAttribute("name");
        if (fullName == null) {
            fullName = "Unknown Name";  // Hoặc xử lý theo cách khác nếu thiếu tên
        }

        // Define the provider ID field based on the provider
        String providerId = registrationId.equalsIgnoreCase("google") ?
                oAuth2User.getAttribute("sub") :
                oAuth2User.getAttribute("id");


        // Determine the auth provider dynamically (Google or Facebook)
        AuthProvider authProvider = AuthProvider.valueOf(registrationId.toUpperCase());

        // Check if a customer exists with the same email and provider
        Optional<Customers> existingCustomer = customersRepository.findByEmailAndAuthProvider(email, authProvider);
        Customers customer;

        if (!existingCustomer.isPresent()) {
            // Create new customer for OAuth2 login
            customer = new Customers();
            customer.setEmail(email);
            customer.setFullName(fullName);
            customer.setAuthProvider(authProvider); // Set provider (Google or Facebook)
            customer.setProviderId(providerId);
            customer.setRole(Role.CUSTOMER);  // Default role

            // No need to store password when using OAuth2
            customer.setPasswordHash(null);

            customersRepository.save(customer);
        } else {
            // If customer exists, update provider ID if necessary
            customer = existingCustomer.get();
            if (!customer.getProviderId().equals(providerId)) {
                customer.setProviderId(providerId);
                customersRepository.save(customer);
            }
        }

        String token = jwtUtil.generateTokenOAuth2(customer);

        log.info("Generated token for user {}: {}", email, token);

        return new CustomOAuth2User(oAuth2User, token, String.valueOf(customer.getCustomerId()));
    }
}

