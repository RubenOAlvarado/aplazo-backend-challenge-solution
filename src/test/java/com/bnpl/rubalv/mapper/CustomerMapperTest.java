package com.bnpl.rubalv.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import com.bnpl.rubalv.dto.response.CustomerResponseDto;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class CustomerMapperTest {
    private final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    public void testToEntity() {
        CreateCustomerRequestDto dto = new CreateCustomerRequestDto();
        dto.setFirstName("Testing");

        Customer customer = customerMapper.toEntity(dto);

        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getSequentialId());
    }

    @Test
    public void testCustomerToResponseDto() {
        Customer customer = new Customer();
        customer.setDateOfBirth(LocalDate.now().minusYears(30));
        customer.setFirstName("Testing");

        CustomerResponseDto responseDto = customerMapper.customerToResponseDto(customer);

        assertNotNull(responseDto);
        assertNull(responseDto.getCreditLineAmount());
        assertNull(responseDto.getAvailableCreditLineAmount());
    }

    @Test
    public void testMapToCustomerResponseDtoWithCreditLine() {
        Customer customer = new Customer();
        customer.setDateOfBirth(LocalDate.now().minusYears(30));
        customer.setFirstName("Testing");

        CreditLine creditLine = new CreditLine();
        creditLine.setAvailableCreditAmount(BigDecimal.valueOf(1000));
        creditLine.setTotalCreditAmount(BigDecimal.valueOf(5000));

        CustomerResponseDto responseDto = customerMapper.mapToCustomerResponseDto(customer, Optional.of(creditLine));

        assertNotNull(responseDto);
        assertEquals(BigDecimal.valueOf(1000), responseDto.getAvailableCreditLineAmount());
        assertEquals(BigDecimal.valueOf(5000), responseDto.getCreditLineAmount());
    }

    @Test
    public void testMapToCustomerResponseDtoWithoutCreditLine() {
        Customer customer = new Customer();
        customer.setDateOfBirth(LocalDate.now().minusYears(30));
        customer.setFirstName("Testing");

        CustomerResponseDto responseDto = customerMapper.mapToCustomerResponseDto(customer, Optional.empty());

        assertNotNull(responseDto);
        assertEquals(BigDecimal.ZERO, responseDto.getAvailableCreditLineAmount());
        assertEquals(BigDecimal.ZERO, responseDto.getCreditLineAmount());
    }
}
