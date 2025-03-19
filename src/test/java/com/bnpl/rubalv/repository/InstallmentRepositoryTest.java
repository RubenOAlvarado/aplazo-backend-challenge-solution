package com.bnpl.rubalv.repository;

import com.bnpl.rubalv.enums.CreditLineStatus;
import com.bnpl.rubalv.enums.InstallmentStatus;
import com.bnpl.rubalv.enums.LoanStatus;
import com.bnpl.rubalv.enums.PaymentScheme;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import com.bnpl.rubalv.model.Installment;
import com.bnpl.rubalv.model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
public class InstallmentRepositoryTest {
    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private CreditLine activeCreditLine;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void setUp(){
        Customer customer = Customer.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .secondLastName("Gómez")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .build();
        entityManager.persistAndFlush(customer);

        activeCreditLine = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("10000.00"))
                .availableCreditAmount(new BigDecimal("5000.00"))
                .status(CreditLineStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(activeCreditLine);
    }

    @Test
    void findByLoan_WhenInstallmentsExist_ReturnsCorrectInstallments() {
        Loan loan = Loan.builder()
                .amount(new BigDecimal("10000.00"))
                .totalAmount(new BigDecimal("11000.00"))
                .status(LoanStatus.ACTIVE)
                .paymentScheme(PaymentScheme.SCHEME_1)
                .interestRate(new BigDecimal("5.00"))
                .commissionAmount(new BigDecimal("100.00"))
                .purchaseDate(LocalDate.of(2024, 3, 1))
                .creditLine(activeCreditLine)
                .build();
        entityManager.persistAndFlush(loan);

        Installment installment1 = Installment.builder()
                .loan(loan)
                .amount(new BigDecimal("2000.00"))
                .scheduledPaymentDate(LocalDate.of(2024, 4, 1))
                .status(InstallmentStatus.PENDING)
                .installmentNumber(1)
                .build();

        Installment installment2 = Installment.builder()
                .loan(loan)
                .amount(new BigDecimal("2000.00"))
                .scheduledPaymentDate(LocalDate.of(2024, 5, 1))
                .status(InstallmentStatus.PENDING)
                .installmentNumber(2)
                .build();

        entityManager.persist(installment1);
        entityManager.persist(installment2);
        entityManager.flush();

        List<Installment> result = installmentRepository.findByLoan(loan);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Installment::getInstallmentNumber)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void findByLoan_WhenNoInstallmentsExist_ReturnsEmptyList() {
        Loan loan = Loan.builder()
                .amount(new BigDecimal("5000.00"))
                .totalAmount(new BigDecimal("5500.00"))
                .status(LoanStatus.ACTIVE)
                .paymentScheme(PaymentScheme.SCHEME_1)
                .interestRate(new BigDecimal("3.50"))
                .commissionAmount(new BigDecimal("50.00"))
                .purchaseDate(LocalDate.of(2024, 3, 10))
                .creditLine(activeCreditLine)
                .build();
        entityManager.persistAndFlush(loan);

        List<Installment> result = installmentRepository.findByLoan(loan);

        assertThat(result).isEmpty();
    }
}
