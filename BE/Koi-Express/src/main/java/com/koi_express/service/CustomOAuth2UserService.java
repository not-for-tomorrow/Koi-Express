package com.koi_express.service;

import com.koi_express.entity.Customers;
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

    public CustomOAuth2UserService(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
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

        if (!existingCustomer.isPresent()) {
            // Create new customer for OAuth2 login
            Customers newCustomer = new Customers();
            newCustomer.setEmail(email);
            newCustomer.setFullName(fullName);
            newCustomer.setAuthProvider(authProvider); // Set provider (Google or Facebook)
            newCustomer.setProviderId(providerId);
            newCustomer.setRole(Role.CUSTOMER);  // Default role

            // No need to store password when using OAuth2
            newCustomer.setPasswordHash(null);

            customersRepository.save(newCustomer);
        } else {
            // If customer exists, update provider ID if necessary
            Customers existing = existingCustomer.get();
            if (!existing.getProviderId().equals(providerId)) {
                existing.setProviderId(providerId);
                customersRepository.save(existing);
            }
        }

        return oAuth2User;
    }
}

