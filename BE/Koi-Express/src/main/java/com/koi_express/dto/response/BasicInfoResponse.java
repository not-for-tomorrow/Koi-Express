package com.koi_express.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicInfoResponse {

    private String fullName;
    private String phoneNumber;
    private String email;
}
