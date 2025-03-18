package com.bnpl.rubalv.dto.response;

import com.bnpl.rubalv.enums.LoanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {
    @Schema(description = "Loan's unique identifier", format = "uuid")
    private UUID id;

    @Schema(description = "Customer's unique identifier", format = "uuid")
    private UUID customerId;

    @Schema(description = "Loan status", implementation = LoanStatus.class)
    private LoanStatus status;

    @Schema(description = "Creation date time as ISO-8601", format = "date-time")
    private Instant createdAt;

    @Schema(description = "Payment plan details")
    private PaymentPlanResponseDto paymentPlan;
}
