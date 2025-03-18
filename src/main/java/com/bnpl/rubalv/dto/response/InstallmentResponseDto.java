package com.bnpl.rubalv.dto.response;

import com.bnpl.rubalv.enums.InstallmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentResponseDto {
    private BigDecimal amount;
    private LocalDate scheduledPaymentDate;
    private InstallmentStatus status;
}
