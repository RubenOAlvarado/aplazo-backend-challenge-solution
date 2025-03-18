package com.bnpl.rubalv.service;

import com.bnpl.rubalv.model.Installment;
import com.bnpl.rubalv.model.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InstallmentService {
    List<Installment> generateInstallments(Loan loan, BigDecimal installmentAmount, List<LocalDate> paymentDates);
}
