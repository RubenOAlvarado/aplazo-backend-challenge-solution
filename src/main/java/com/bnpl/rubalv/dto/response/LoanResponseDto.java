package com.bnpl.rubalv.dto.response;

import com.bnpl.rubalv.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {
    private UUID id;
    private UUID customerId;
    private LoanStatus status;
    private OffsetDateTime createdAt;
    private PaymentPlanResponseDto paymentPlan;
}
