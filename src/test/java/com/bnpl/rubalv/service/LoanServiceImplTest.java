package com.bnpl.rubalv.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import com.bnpl.rubalv.dto.request.LoanRequestDto;
import com.bnpl.rubalv.dto.response.LoanResponseDto;
import com.bnpl.rubalv.enums.LoanStatus;
import com.bnpl.rubalv.enums.PaymentScheme;
import com.bnpl.rubalv.exception.ClientWithoutCreditLineException;
import com.bnpl.rubalv.exception.InsufficientCreditException;
import com.bnpl.rubalv.exception.LoanNotFoundException;
import com.bnpl.rubalv.mapper.LoanMapper;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import com.bnpl.rubalv.model.Installment;
import com.bnpl.rubalv.model.Loan;
import com.bnpl.rubalv.repository.LoanRepository;
import com.bnpl.rubalv.strategy.PaymentSchemeStrategy;
import com.bnpl.rubalv.utils.helpers.DateHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoanServiceImplTest {
    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private CreditLineService creditLineService;

    @Mock
    private InstallmentService installmentService;

    @Mock
    private PaymentSchemeStrategy paymentSchemeStrategy;

    @Mock
    private DateHelper dateHelper;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void registerLoan_Success() {
        LoanRequestDto loanRequest = new LoanRequestDto();
        UUID customerId = UUID.randomUUID();
        loanRequest.setCustomerId(customerId);
        BigDecimal requestAmount = BigDecimal.valueOf(1000);
        loanRequest.setAmount(requestAmount);

        Customer customer = new Customer();
        customer.setId(customerId);
        when(customerService.findCustomerById(customerId)).thenReturn(customer);

        CreditLine creditLine = new CreditLine();
        creditLine.setAvailableCreditAmount(BigDecimal.valueOf(1500));
        when(creditLineService.getCustomerCreditLine(customer)).thenReturn(creditLine);

        PaymentScheme paymentScheme = mock(PaymentScheme.class);
        when(paymentScheme.getInterestRate()).thenReturn(BigDecimal.valueOf(0.10));
        when(paymentScheme.getNumberOfPayments()).thenReturn(5);
        when(paymentSchemeStrategy.determinePaymentSchema(customer)).thenReturn(paymentScheme);

        List<LocalDate> schedule = Arrays.asList(
                LocalDate.now().plusMonths(1),
                LocalDate.now().plusMonths(2),
                LocalDate.now().plusMonths(3),
                LocalDate.now().plusMonths(4),
                LocalDate.now().plusMonths(5)
        );
        when(dateHelper.generatePaymentSchedule(any(LocalDate.class), eq(paymentScheme))).thenReturn(schedule);

        BigDecimal expectedCommission = BigDecimal.valueOf(100).setScale(2);
        BigDecimal expectedTotal = BigDecimal.valueOf(1100).setScale(2);
        BigDecimal expectedInstallment = BigDecimal.valueOf(220).setScale(2);

        List<Installment> installments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            installments.add(new Installment());
        }
        when(installmentService.generateInstallments(any(Loan.class), eq(expectedInstallment), eq(schedule)))
                .thenReturn(installments);

        Loan loanToSave = Loan.builder()
                .creditLine(creditLine)
                .amount(requestAmount)
                .status(LoanStatus.ACTIVE)
                .paymentScheme(paymentScheme)
                .interestRate(BigDecimal.valueOf(0.10))
                .purchaseDate(LocalDate.now())
                .installments(new ArrayList<>())
                .createdAt(OffsetDateTime.now())
                .build();
        Loan savedLoan = Loan.builder()
                .id(UUID.randomUUID())
                .creditLine(creditLine)
                .amount(requestAmount)
                .status(LoanStatus.ACTIVE)
                .paymentScheme(paymentScheme)
                .interestRate(BigDecimal.valueOf(0.10))
                .purchaseDate(LocalDate.now())
                .installments(installments)
                .commissionAmount(expectedCommission)
                .totalAmount(expectedTotal)
                .createdAt(OffsetDateTime.now())
                .build();
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        LoanResponseDto responseDto = new LoanResponseDto();
        when(loanMapper.toResponseDto(savedLoan.getId(), customerId, savedLoan.getStatus(),
                savedLoan.getCreatedAt().toInstant(), savedLoan.getCommissionAmount(), savedLoan.getInstallments()))
                .thenReturn(responseDto);

        LoanResponseDto result = loanService.registerLoan(loanRequest);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(responseDto);

        verify(customerService).findCustomerById(customerId);
        verify(creditLineService).getCustomerCreditLine(customer);
        verify(paymentSchemeStrategy).determinePaymentSchema(customer);
        verify(loanRepository).save(any(Loan.class));
        verify(creditLineService).updateCreditLine(creditLine, requestAmount);
        verify(loanMapper).toResponseDto(savedLoan.getId(), customerId, savedLoan.getStatus(),
                savedLoan.getCreatedAt().toInstant(), savedLoan.getCommissionAmount(), savedLoan.getInstallments());
    }

    @Test
    void registerLoan_NoCreditLine_ThrowsException() {
        LoanRequestDto loanRequest = new LoanRequestDto();
        UUID customerId = UUID.randomUUID();
        loanRequest.setCustomerId(customerId);
        loanRequest.setAmount(BigDecimal.valueOf(1000));

        Customer customer = new Customer();
        customer.setId(customerId);
        when(customerService.findCustomerById(customerId)).thenReturn(customer);
        when(creditLineService.getCustomerCreditLine(customer)).thenReturn(null);

        assertThatThrownBy(() -> loanService.registerLoan(loanRequest))
                .isInstanceOf(ClientWithoutCreditLineException.class)
                .hasMessageContaining("Client does not have an active credit line");

        verify(customerService).findCustomerById(customerId);
        verify(creditLineService).getCustomerCreditLine(customer);
        verifyNoInteractions(paymentSchemeStrategy);
    }

    @Test
    void registerLoan_InsufficientCredit_ThrowsException() {
        LoanRequestDto loanRequest = new LoanRequestDto();
        UUID customerId = UUID.randomUUID();
        loanRequest.setCustomerId(customerId);
        loanRequest.setAmount(BigDecimal.valueOf(2000));

        Customer customer = new Customer();
        customer.setId(customerId);
        when(customerService.findCustomerById(customerId)).thenReturn(customer);

        CreditLine creditLine = new CreditLine();
        creditLine.setAvailableCreditAmount(BigDecimal.valueOf(1500));
        when(creditLineService.getCustomerCreditLine(customer)).thenReturn(creditLine);

        assertThatThrownBy(() -> loanService.registerLoan(loanRequest))
                .isInstanceOf(InsufficientCreditException.class)
                .hasMessageContaining("Amount surpass available credit");

        verify(customerService).findCustomerById(customerId);
        verify(creditLineService).getCustomerCreditLine(customer);
    }

    @Test
    void getLoanById_Success() {
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(customerId);
        CreditLine creditLine = new CreditLine();
        creditLine.setCustomer(customer);

        Loan loan = Loan.builder()
                .id(loanId)
                .creditLine(creditLine)
                .amount(BigDecimal.valueOf(1000))
                .status(LoanStatus.ACTIVE)
                .purchaseDate(LocalDate.now())
                .installments(new ArrayList<>())
                .createdAt(OffsetDateTime.now())
                .build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        LoanResponseDto responseDto = new LoanResponseDto();
        when(loanMapper.toResponseDto(loan.getId(), customerId, loan.getStatus(),
                loan.getCreatedAt().toInstant(), loan.getCommissionAmount(), loan.getInstallments()))
                .thenReturn(responseDto);

        LoanResponseDto result = loanService.getLoanById(loanId);

        assertThat(result).isEqualTo(responseDto);
        verify(loanRepository).findById(loanId);
        verify(loanMapper).toResponseDto(loan.getId(), customerId, loan.getStatus(),
                loan.getCreatedAt().toInstant(), loan.getCommissionAmount(), loan.getInstallments());
    }

    @Test
    void getLoanById_NotFound_ThrowsException() {
        UUID loanId = UUID.randomUUID();
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.getLoanById(loanId))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessageContaining(loanId.toString());

        verify(loanRepository).findById(loanId);
    }
}
