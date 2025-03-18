package com.bnpl.rubalv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPlanResponseDto {
    private BigDecimal commissionAmount;
    private List<InstallmentResponseDto> installments;
}
