package com.bnpl.rubalv.service;

import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import com.bnpl.rubalv.dto.response.CustomerRegistrationResult;
import com.bnpl.rubalv.dto.response.CustomerResponseDto;
import com.bnpl.rubalv.exception.CustomerNotFoundException;
import com.bnpl.rubalv.mapper.CustomerMapper;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import com.bnpl.rubalv.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;
    private final CreditLineService creditLineService;
    private final CustomerMapper customerMapper;
    private final JwtTokenService jwtTokenService;

    @Override
    @Transactional
    public CustomerRegistrationResult registerCustomer(CreateCustomerRequestDto customerRequest){
        log.info("Start customer registration for customer: {}", customerRequest.getFirstName());
        Customer customerEntity = customerMapper.toEntity(customerRequest);
        Customer savedCustomer = customerRepository.save(customerEntity);
        CreditLine clientCreditLine;
        try {
            log.info("Customer created, determining credit line: {}", savedCustomer.getId());
            clientCreditLine = creditLineService.createCreditLine(savedCustomer);
        } catch (IllegalArgumentException e) {
            log.error("Error while creating new customer: {}", e.getMessage(), e);
            throw e;
        }

        log.info("Customer and his credit line successfully created. Creating JWT");
        String token = jwtTokenService.generateToken(savedCustomer.getId());
        CustomerResponseDto customerResponseDto = customerMapper.mapToCustomerResponseDto(savedCustomer, Optional.of(clientCreditLine));

        return new CustomerRegistrationResult(customerResponseDto, token);
    }

    @Override
    public CustomerResponseDto getCustomerById(UUID id){
        return customerRepository.findById(id).map(customer -> {
                    Optional<CreditLine> creditLine = Optional.ofNullable(creditLineService.getCustomerCreditLine(customer));
                    return customerMapper.mapToCustomerResponseDto(customer, creditLine);
                })
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer findCustomerById(UUID id){
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }
}
