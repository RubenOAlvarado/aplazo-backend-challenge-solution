package com.bnpl.rubalv.service;

import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import com.bnpl.rubalv.dto.response.CustomerResponseDto;
import com.bnpl.rubalv.model.Customer;

import java.util.UUID;

public interface CustomerService {
    CustomerResponseDto registerCustomer(CreateCustomerRequestDto customerRequest);
    CustomerResponseDto getCustomerById(UUID id);
    Customer findCustomerById(UUID id);
}
