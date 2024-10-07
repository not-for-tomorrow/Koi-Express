package com.koi_express.service.customer;

import java.util.Collections;
import java.util.Optional;

import com.koi_express.entity.account.SystemAccount;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.DeliveringStaffRepository;
import com.koi_express.repository.SystemAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Autowired
    private DeliveringStaffRepository deliveringStaffRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {

        Optional<Customers> customerOpt = customersRepository.findByPhoneNumber(phoneNumber);
        if (customerOpt.isPresent()) {
            Customers customers = customerOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    customers.getPhoneNumber(), customers.getPasswordHash(), Collections.emptyList());
        }

        Optional<SystemAccount> systemAccountOpt = systemAccountRepository.findByPhoneNumber(phoneNumber);
        if (systemAccountOpt.isPresent()) {
            SystemAccount systemAccount = systemAccountOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    systemAccount.getPhoneNumber(), systemAccount.getPasswordHash(), Collections.emptyList());
        }

        Optional<DeliveringStaff> deliveringStaffOpt = deliveringStaffRepository.findByPhoneNumber(phoneNumber);
        if (deliveringStaffOpt.isPresent()) {
            DeliveringStaff deliveringStaff = deliveringStaffOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    deliveringStaff.getPhoneNumber(), deliveringStaff.getPasswordHash(), Collections.emptyList());
        }

        throw new UsernameNotFoundException("User not found with phone number: " + phoneNumber);
    }
}
