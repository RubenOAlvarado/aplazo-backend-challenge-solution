package com.bnpl.rubalv.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.bnpl.rubalv.dto.response.LoanResponseDto;
import com.bnpl.rubalv.enums.InstallmentStatus;
import com.bnpl.rubalv.enums.LoanStatus;
import com.bnpl.rubalv.model.Installment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class LoanMapperTest {
    private final LoanMapper loanMapper = new LoanMapper();

    @Test
    public void testToResponseDto() {
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        LoanStatus status = LoanStatus.ACTIVE;
        Instant createdAt = Instant.now();
        BigDecimal commissionAmount = BigDecimal.valueOf(100);
        List<Installment> installments = List.of();

        LoanResponseDto responseDto = loanMapper.toResponseDto(loanId, customerId, status, createdAt, commissionAmount, installments);

        assertNotNull(responseDto);
        assertEquals(loanId, responseDto.getId());
        assertEquals(customerId, responseDto.getCustomerId());
        assertEquals(status, responseDto.getStatus());
        assertEquals(createdAt, responseDto.getCreatedAt());
        assertNotNull(responseDto.getPaymentPlan());
        assertEquals(commissionAmount, responseDto.getPaymentPlan().getCommissionAmount());
        assertNotNull(responseDto.getPaymentPlan().getInstallments());
        assertEquals(0, responseDto.getPaymentPlan().getInstallments().size());
    }
}
