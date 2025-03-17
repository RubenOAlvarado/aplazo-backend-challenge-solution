package com.bnpl.rubalv.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoanRequestDto {
    @NotNull
    private UUID customerId;

    @NotNull
    @Positive
    private BigDecimal purchaseAmount;
}
