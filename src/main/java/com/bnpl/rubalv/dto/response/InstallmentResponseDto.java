package com.bnpl.rubalv.dto.response;

import com.bnpl.rubalv.enums.InstallmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentResponseDto {
    @Schema(description = "Installment amount to be paid", minimum = "0", exclusiveMinimum = true)
    private BigDecimal amount;

    @Schema(description = "Scheduled date for the installment to be", format = "date")
    private LocalDate scheduledPaymentDate;

    @Schema(description = "Installment status", implementation = InstallmentStatus.class)
    private InstallmentStatus status;
}
