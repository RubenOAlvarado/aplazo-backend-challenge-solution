package com.bnpl.rubalv.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPlanResponseDto {
    @Schema(description = "Commission amount applied to loan", minimum = "0", exclusiveMinimum = true)
    private BigDecimal commissionAmount;

    private List<InstallmentResponseDto> installments;
}
