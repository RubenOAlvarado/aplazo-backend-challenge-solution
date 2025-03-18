package com.bnpl.rubalv.service;

import com.bnpl.rubalv.enums.InstallmentStatus;
import com.bnpl.rubalv.model.Installment;
import com.bnpl.rubalv.model.Loan;
import com.bnpl.rubalv.repository.InstallmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstallmentServiceImpl implements InstallmentService {
    private final InstallmentRepository installmentRepository;

    @Override
    public List<Installment> generateInstallments(Loan loan, BigDecimal installmentAmount, List<LocalDate> paymentDates) {
        log.info("Generating loan installments");
        List<Installment> installments = new ArrayList<>();
        for (int i = 0; i < paymentDates.size(); i++) {
            Installment installment = Installment.builder()
                    .amount(installmentAmount)
                    .scheduledPaymentDate(paymentDates.get(i))
                    .status(InstallmentStatus.PENDING)
                    .installmentNumber(i + 1)
                    .loan(loan)
                    .build();
            installments.add(installment);
        }
        log.info("Loan installments: {}", installments.size());
        return installments;
    }
}
