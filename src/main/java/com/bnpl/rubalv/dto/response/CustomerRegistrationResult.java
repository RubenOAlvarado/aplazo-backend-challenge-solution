package com.bnpl.rubalv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistrationResult {
    private CustomerResponseDto customer;
    private String token;
}
