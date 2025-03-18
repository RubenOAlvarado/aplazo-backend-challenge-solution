package com.bnpl.rubalv.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class LoanRequestDto {
    @Schema(description = "Customer's unique identifier", format = "uuid")
    @NotNull
    private UUID customerId;

    @Schema(description = "Requested loan amount", minimum = "0", exclusiveMinimum = true)
    @NotNull
    @Positive
    private BigDecimal amount;
}
