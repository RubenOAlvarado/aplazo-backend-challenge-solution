package com.bnpl.rubalv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.bnpl.rubalv.enums.InstallmentStatus;
import com.bnpl.rubalv.model.Installment;
import com.bnpl.rubalv.model.Loan;
import org.junit.jupiter.api.Test;

public class InstallmentServiceImplTest {

    private final InstallmentService installmentService = new InstallmentServiceImpl();

    @Test
    void generateInstallments_WithValidData_CreatesCorrectInstallments() {
        Loan loan = new Loan();
        BigDecimal amount = BigDecimal.valueOf(1000);
        List<LocalDate> paymentDates = Arrays.asList(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 2, 1),
                LocalDate.of(2023, 3, 1)
        );

        List<Installment> result = installmentService.generateInstallments(loan, amount, paymentDates);

        assertThat(result).hasSize(3);

        Installment first = result.get(0);
        assertThat(first.getAmount()).isEqualTo(amount);
        assertThat(first.getScheduledPaymentDate()).isEqualTo(paymentDates.get(0));
        assertThat(first.getStatus()).isEqualTo(InstallmentStatus.PENDING);
        assertThat(first.getInstallmentNumber()).isEqualTo(1);
        assertThat(first.getLoan()).isEqualTo(loan);

        assertThat(result.get(1).getInstallmentNumber()).isEqualTo(2);
        assertThat(result.get(2).getInstallmentNumber()).isEqualTo(3);
    }

    @Test
    void generateInstallments_EmptyPaymentDates_ReturnsEmptyList() {
        Loan loan = new Loan();
        List<Installment> result = installmentService.generateInstallments(
                loan,
                BigDecimal.TEN,
                Collections.emptyList()
        );

        assertThat(result).isEmpty();
    }

    @Test
    void generateInstallments_NullPaymentDates_ThrowsException() {
        Loan loan = new Loan();

        assertThrows(NullPointerException.class, () -> {
            installmentService.generateInstallments(loan, BigDecimal.ONE, null);
        });
    }

    @Test
    void generateInstallments_VerifyAllFieldsSetCorrectly() {
        Loan loan = new Loan();
        BigDecimal amount = BigDecimal.valueOf(500);
        LocalDate testDate = LocalDate.of(2023, 5, 15);

        List<Installment> result = installmentService.generateInstallments(
                loan,
                amount,
                List.of(testDate)
        );

        Installment installment = result.get(0);
        assertThat(installment)
                .usingRecursiveComparison()
                .isEqualTo(Installment.builder()
                        .amount(amount)
                        .scheduledPaymentDate(testDate)
                        .status(InstallmentStatus.PENDING)
                        .installmentNumber(1)
                        .loan(loan)
                        .build());
    }
}
