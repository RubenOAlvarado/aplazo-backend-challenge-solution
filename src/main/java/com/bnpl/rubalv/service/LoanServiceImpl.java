package com.bnpl.rubalv.service;

import com.bnpl.rubalv.dto.request.LoanRequestDto;
import com.bnpl.rubalv.dto.response.LoanResponseDto;
import com.bnpl.rubalv.enums.LoanStatus;
import com.bnpl.rubalv.enums.PaymentScheme;
import com.bnpl.rubalv.exception.BussinessException;
import com.bnpl.rubalv.exception.ClientWithoutCreditLineException;
import com.bnpl.rubalv.exception.InsufficientCreditException;
import com.bnpl.rubalv.exception.LoanNotFoundException;
import com.bnpl.rubalv.mapper.LoanMapper;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import com.bnpl.rubalv.model.Installment;
import com.bnpl.rubalv.model.Loan;
import com.bnpl.rubalv.repository.CustomerRepository;
import com.bnpl.rubalv.repository.LoanRepository;
import com.bnpl.rubalv.strategy.PaymentSchemeStrategy;
import com.bnpl.rubalv.utils.helpers.DateHelper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final CustomerService customerService;
    private final CreditLineService creditLineService;
    private final InstallmentService installmentService;
    private final PaymentSchemeStrategy paymentSchemeStrategy;
    private final DateHelper dateHelper;
    private final LoanMapper loanMapper;

    @Override
    @Transactional
    public LoanResponseDto registerLoan(LoanRequestDto loanRequest) {
        Customer customer = customerService.findCustomerById(loanRequest.getCustomerId());
        CreditLine clientsCreditLine = creditLineService.getCustomerCreditLine(customer);
        if(clientsCreditLine == null){
            log.error("Customer {} does not have an active credit line", loanRequest.getCustomerId());
            throw new ClientWithoutCreditLineException("Client does not have an active credit line");
        }

        try{
            validatePurchaseAmount(loanRequest.getPurchaseAmount(), clientsCreditLine);
        }catch(InsufficientCreditException ex){
            log.warn(
                    "Loan amount exceeds available credit. Customer: {}, Amount: {}, Available: {}",
                    loanRequest.getCustomerId(),
                    loanRequest.getPurchaseAmount(),
                    clientsCreditLine.getAvailableCreditAmount()
                    );
            throw ex;
        }

        PaymentScheme scheme = paymentSchemeStrategy.determinePaymentSchema(customer);

        Loan loan = buildLoanEntity(loanRequest, clientsCreditLine, scheme);
        calculateLoanDetails(loan, loanRequest.getPurchaseAmount(), scheme);
        Loan savedLoan = loanRepository.save(loan);

        creditLineService.updateCreditLine(clientsCreditLine, loanRequest.getPurchaseAmount());

        return loanMapper.toResponseDto(savedLoan.getId(), loanRequest.getCustomerId(), savedLoan.getStatus(), savedLoan.getCreatedAt(), savedLoan.getCommissionAmount(), savedLoan.getInstallments());
    }

    @Override
    public LoanResponseDto getLoanById(UUID id){
        return loanRepository.findById(id)
                .map(loan ->
                        loanMapper.toResponseDto(loan.getId(), loan.getCreditLine().getCustomer().getId(), loan.getStatus(), loan.getCreatedAt(), loan.getCommissionAmount(), loan.getInstallments())
                ).orElseThrow(() -> new LoanNotFoundException(id));
    }

    private void validatePurchaseAmount(BigDecimal amount, CreditLine creditLine){
        if(amount.compareTo(creditLine.getAvailableCreditAmount()) > 0){
            throw new InsufficientCreditException("Amount surpass available credit. Available credit: "+ creditLine.getAvailableCreditAmount());
        }
    }

    private void calculateLoanDetails(Loan loan, BigDecimal amount, PaymentScheme scheme){
        log.info("Loan details calculation");
        BigDecimal interestRate = scheme.getInterestRate();
        BigDecimal commission = amount.multiply(interestRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = amount.add(commission);
        BigDecimal installmentAmount = totalAmount.divide(BigDecimal.valueOf(scheme.getNumberOfPayments()), 2, RoundingMode.HALF_UP);
        List<Installment> installments = installmentService.generateInstallments(
                loan, installmentAmount, dateHelper.generatePaymentSchedule(LocalDate.now(), scheme)
        );

        LoanCalculation loanCalculation = new LoanCalculation(
                commission,
                totalAmount,
                installmentAmount,
                installments
        );
        log.info("Loan successfully calculated: {}", loanCalculation.totalAmount());
        loan.setInstallments(loanCalculation.installments());
        loan.setCommissionAmount(loanCalculation.commission());
        loan.setTotalAmount(loanCalculation.totalAmount());
    }

    private Loan buildLoanEntity(LoanRequestDto request, CreditLine creditLine, PaymentScheme scheme){
        return Loan.builder()
                .creditLine(creditLine)
                .amount(request.getPurchaseAmount())
                .status(LoanStatus.ACTIVE)
                .paymentScheme(scheme)
                .interestRate(scheme.getInterestRate())
                .purchaseDate(LocalDate.now())
                .installments(new ArrayList<>())
                .build();
    }

    private record LoanCalculation(
            BigDecimal commission,
            BigDecimal totalAmount,
            BigDecimal installmentAmount,
            List<Installment> installments
    ){}
}
