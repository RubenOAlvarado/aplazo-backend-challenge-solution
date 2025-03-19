package com.bnpl.rubalv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bnpl.rubalv.constants.CreditLineConstants;
import com.bnpl.rubalv.enums.CreditLineStatus;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import com.bnpl.rubalv.repository.CreditLineRepository;
import com.bnpl.rubalv.utils.helpers.DateHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CreditLineServiceImplTest {
    @Mock
    private CreditLineRepository creditLineRepository;

    @Mock
    private DateHelper dateHelper;

    @InjectMocks
    private CreditLineServiceImpl creditLineService;

    @Test
    void createCreditLine_YoungAdult_Success() {
        Customer customer = new Customer();
        customer.setDateOfBirth(LocalDate.now());

        when(dateHelper.calculateAge(any(LocalDate.class))).thenReturn(20);
        when(creditLineRepository.save(any(CreditLine.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditLine result = creditLineService.createCreditLine(customer);

        assertThat(result.getTotalCreditAmount()).isEqualTo(CreditLineConstants.CREDIT_AMOUNT_YOUNG_ADULT);
        assertThat(result.getAvailableCreditAmount()).isEqualTo(CreditLineConstants.CREDIT_AMOUNT_YOUNG_ADULT);
        assertThat(result.getStatus()).isEqualTo(CreditLineStatus.ACTIVE);
        verify(creditLineRepository).save(any(CreditLine.class));
    }

    @Test
    void createCreditLine_AgeBelowMinimum_ThrowsException() {
        Customer customer = new Customer();
        customer.setDateOfBirth(LocalDate.now());

        when(dateHelper.calculateAge(any(LocalDate.class))).thenReturn(17);

        assertThrows(IllegalArgumentException.class, () -> creditLineService.createCreditLine(customer));
        verify(creditLineRepository, never()).save(any(CreditLine.class));
    }

    @Test
    void getCustomerCreditLine_ActiveExists_ReturnsCreditLine() {
        Customer customer = new Customer();
        CreditLine expectedCreditLine = new CreditLine();

        when(creditLineRepository.findByCustomerAndStatusEquals(customer, CreditLineStatus.ACTIVE))
                .thenReturn(Optional.of(expectedCreditLine));

        CreditLine result = creditLineService.getCustomerCreditLine(customer);

        assertThat(result).isEqualTo(expectedCreditLine);
    }

    @Test
    void getCustomerCreditLine_NoActive_ReturnsNull() {
        Customer customer = new Customer();

        when(creditLineRepository.findByCustomerAndStatusEquals(customer, CreditLineStatus.ACTIVE))
                .thenReturn(Optional.empty());

        CreditLine result = creditLineService.getCustomerCreditLine(customer);

        assertThat(result).isNull();
    }

    @Test
    void updateCreditLine_ValidAmount_UpdatesCorrectly() {
        CreditLine creditLine = CreditLine.builder()
                .availableCreditAmount(BigDecimal.valueOf(1000))
                .build();
        BigDecimal amount = BigDecimal.valueOf(200);

        when(creditLineRepository.save(creditLine)).thenReturn(creditLine);

        creditLineService.updateCreditLine(creditLine, amount);

        assertThat(creditLine.getAvailableCreditAmount()).isEqualTo(BigDecimal.valueOf(800));
        verify(creditLineRepository).save(creditLine);
    }

    @Test
    void determineCreditLineAmount_Senior_CorrectAmount() {
        Customer customer = new Customer();
        customer.setDateOfBirth(LocalDate.now());

        when(dateHelper.calculateAge(any(LocalDate.class))).thenReturn(60);
        when(creditLineRepository.save(any(CreditLine.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditLine result = creditLineService.createCreditLine(customer);

        assertThat(result.getTotalCreditAmount()).isEqualTo(CreditLineConstants.CREDIT_AMOUNT_SENIOR);
    }
}
