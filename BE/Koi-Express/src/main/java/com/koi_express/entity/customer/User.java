package com.koi_express.entity.customer;

import com.koi_express.entity.account.SystemAccount;
import com.koi_express.enums.Role;

public interface User {

    Long getId();

    String getPhoneNumber();

    String getEmail();

    String getPasswordHash();

    String getFullName();

    Role getRole();
}
