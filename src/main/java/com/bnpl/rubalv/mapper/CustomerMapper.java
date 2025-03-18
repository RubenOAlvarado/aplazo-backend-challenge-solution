package com.bnpl.rubalv.mapper;

import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import com.bnpl.rubalv.dto.response.CustomerResponseDto;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;
import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sequentialId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Customer toEntity(CreateCustomerRequestDto customerRequestDto);

    @Mapping(target =  "creditLineAmount", ignore = true)
    @Mapping(target = "availableCreditLineAmount", ignore = true)
    CustomerResponseDto customerToResponseDto(Customer customer);

   default CustomerResponseDto mapToCustomerResponseDto(Customer customer, Optional<CreditLine> creditLineOptional){
        CustomerResponseDto responseDto = new CustomerResponseDto();
        responseDto.setId(customer.getId());
        responseDto.setCreatedAt(customer.getCreatedAt());
        creditLineOptional.ifPresentOrElse(
                creditLine -> {
                    responseDto.setAvailableCreditLineAmount(creditLine.getAvailableCreditAmount());
                    responseDto.setCreditLineAmount(creditLine.getTotalCreditAmount());
                },
                () -> {
                    responseDto.setAvailableCreditLineAmount(BigDecimal.ZERO);
                    responseDto.setCreditLineAmount(BigDecimal.ZERO);
                }
        );

        return responseDto;
    }
}
