package com.bnpl.rubalv.mapper;

import com.bnpl.rubalv.dto.response.InstallmentResponseDto;
import com.bnpl.rubalv.dto.response.LoanResponseDto;
import com.bnpl.rubalv.dto.response.PaymentPlanResponseDto;
import com.bnpl.rubalv.enums.LoanStatus;
import com.bnpl.rubalv.model.Installment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class LoanMapper {
    public LoanResponseDto toResponseDto(UUID id, UUID customerId, LoanStatus status, Instant createdAt, BigDecimal commissionAmount, List<Installment> installments){
        LoanResponseDto responseDto = new LoanResponseDto();
        responseDto.setId(id);
        responseDto.setCustomerId(customerId);
        responseDto.setStatus(status);
        responseDto.setCreatedAt(createdAt);
        PaymentPlanResponseDto paymentPlan = new PaymentPlanResponseDto();
        paymentPlan.setCommissionAmount(commissionAmount);
        List<InstallmentResponseDto> installmentsResponseDtoList = installments.stream()
                .map(installment ->
                        new InstallmentResponseDto(installment.getAmount(), installment.getScheduledPaymentDate(), installment.getStatus()))
                .toList();
        paymentPlan.setInstallments(installmentsResponseDtoList);
        responseDto.setPaymentPlan(paymentPlan);

        return responseDto;
    }
}
