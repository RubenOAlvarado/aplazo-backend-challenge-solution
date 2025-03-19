package com.bnpl.rubalv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import com.bnpl.rubalv.dto.response.CustomerRegistrationResult;
import com.bnpl.rubalv.dto.response.CustomerResponseDto;
import com.bnpl.rubalv.exception.CustomerNotFoundException;
import com.bnpl.rubalv.mapper.CustomerMapper;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import com.bnpl.rubalv.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CreditLineService creditLineService;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void registerCustomer_Success() {
        CreateCustomerRequestDto requestDto = new CreateCustomerRequestDto();
        requestDto.setFirstName("Juan");

        Customer customerEntity = new Customer();
        when(customerMapper.toEntity(requestDto)).thenReturn(customerEntity);

        Customer savedCustomer = new Customer();
        UUID customerId = UUID.randomUUID();
        savedCustomer.setId(customerId);
        when(customerRepository.save(customerEntity)).thenReturn(savedCustomer);

        CreditLine creditLine = new CreditLine();
        when(creditLineService.createCreditLine(savedCustomer)).thenReturn(creditLine);

        String jwtToken = "jwt-token";
        when(jwtTokenService.generateToken(customerId)).thenReturn(jwtToken);

        CustomerResponseDto responseDto = new CustomerResponseDto();
        when(customerMapper.mapToCustomerResponseDto(savedCustomer, Optional.of(creditLine)))
                .thenReturn(responseDto);

        CustomerRegistrationResult result = customerService.registerCustomer(requestDto);

        assertThat(result.getCustomer()).isEqualTo(responseDto);
        assertThat(result.getToken()).isEqualTo(jwtToken);

        verify(customerMapper).toEntity(requestDto);
        verify(customerRepository).save(customerEntity);
        verify(creditLineService).createCreditLine(savedCustomer);
        verify(jwtTokenService).generateToken(customerId);
        verify(customerMapper).mapToCustomerResponseDto(savedCustomer, Optional.of(creditLine));
    }

    @Test
    void registerCustomer_WhenCreditLineCreationFails_ThrowsException() {
        CreateCustomerRequestDto requestDto = new CreateCustomerRequestDto();
        requestDto.setFirstName("Maria");

        Customer customerEntity = new Customer();
        when(customerMapper.toEntity(requestDto)).thenReturn(customerEntity);

        Customer savedCustomer = new Customer();
        UUID customerId = UUID.randomUUID();
        savedCustomer.setId(customerId);
        when(customerRepository.save(customerEntity)).thenReturn(savedCustomer);

        when(creditLineService.createCreditLine(savedCustomer))
                .thenThrow(new IllegalArgumentException("Error al crear la línea de crédito"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.registerCustomer(requestDto));
        assertThat(exception.getMessage()).isEqualTo("Error al crear la línea de crédito");

        verify(creditLineService).createCreditLine(savedCustomer);
    }

    @Test
    void getCustomerById_Success() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CreditLine creditLine = new CreditLine();
        when(creditLineService.getCustomerCreditLine(customer)).thenReturn(creditLine);

        CustomerResponseDto responseDto = new CustomerResponseDto();
        when(customerMapper.mapToCustomerResponseDto(customer, Optional.of(creditLine)))
                .thenReturn(responseDto);

        CustomerResponseDto result = customerService.getCustomerById(customerId);

        assertThat(result).isEqualTo(responseDto);
        verify(customerRepository).findById(customerId);
        verify(creditLineService).getCustomerCreditLine(customer);
        verify(customerMapper).mapToCustomerResponseDto(customer, Optional.of(creditLine));
    }

    @Test
    void getCustomerById_CustomerNotFound_ThrowsException() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomerById(customerId));
        assertThat(exception.getMessage()).contains(customerId.toString());
    }

    @Test
    void findCustomerById_Success() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Customer result = customerService.findCustomerById(customerId);
        assertThat(result).isEqualTo(customer);
        verify(customerRepository).findById(customerId);
    }

    @Test
    void findCustomerById_CustomerNotFound_ThrowsException() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.findCustomerById(customerId));
        assertThat(exception.getMessage()).contains(customerId.toString());
    }
}
