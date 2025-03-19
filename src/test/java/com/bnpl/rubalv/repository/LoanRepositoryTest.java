package com.bnpl.rubalv.repository;

import com.bnpl.rubalv.enums.CreditLineStatus;
import com.bnpl.rubalv.enums.InstallmentStatus;
import com.bnpl.rubalv.enums.LoanStatus;
import com.bnpl.rubalv.enums.PaymentScheme;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import com.bnpl.rubalv.model.Installment;
import com.bnpl.rubalv.model.Loan;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
public class LoanRepositoryTest {
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

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

    @Test
    void saveLoanAndFindById() {
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .secondLastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        entityManager.persistAndFlush(customer);

        CreditLine creditLine = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("10000.00"))
                .availableCreditAmount(new BigDecimal("8000.00"))
                .status(CreditLineStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(creditLine);

        Loan loan = Loan.builder()
                .creditLine(creditLine)
                .amount(new BigDecimal("5000.00"))
                .totalAmount(new BigDecimal("5500.00"))
                .status(LoanStatus.ACTIVE)
                .paymentScheme(PaymentScheme.SCHEME_1)
                .interestRate(new BigDecimal("4.50"))
                .commissionAmount(new BigDecimal("100.00"))
                .purchaseDate(LocalDate.of(2024, 3, 15))
                .build();
        entityManager.persistAndFlush(loan);

        Optional<Loan> foundLoan = loanRepository.findById(loan.getId());
        assertThat(foundLoan).isPresent();
        assertThat(foundLoan.get().getId()).isEqualTo(loan.getId());
        assertThat(foundLoan.get().getCreditLine().getId()).isEqualTo(creditLine.getId());
    }

    @Test
    void findAllLoans() {
        Customer customer = Customer.builder()
                .firstName("Alice")
                .lastName("Wonderland")
                .secondLastName("Dream")
                .dateOfBirth(LocalDate.of(1985, 6, 15))
                .build();
        entityManager.persistAndFlush(customer);

        CreditLine creditLine = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("15000.00"))
                .availableCreditAmount(new BigDecimal("15000.00"))
                .status(CreditLineStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(creditLine);

        Loan loan1 = Loan.builder()
                .creditLine(creditLine)
                .amount(new BigDecimal("7000.00"))
                .totalAmount(new BigDecimal("7700.00"))
                .status(LoanStatus.ACTIVE)
                .paymentScheme(PaymentScheme.SCHEME_2)
                .interestRate(new BigDecimal("5.00"))
                .commissionAmount(new BigDecimal("150.00"))
                .purchaseDate(LocalDate.of(2024, 4, 1))
                .build();

        Loan loan2 = Loan.builder()
                .creditLine(creditLine)
                .amount(new BigDecimal("3000.00"))
                .totalAmount(new BigDecimal("3300.00"))
                .status(LoanStatus.ACTIVE)
                .paymentScheme(PaymentScheme.SCHEME_1)
                .interestRate(new BigDecimal("4.00"))
                .commissionAmount(new BigDecimal("50.00"))
                .purchaseDate(LocalDate.of(2024, 4, 15))
                .build();

        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.flush();

        List<Loan> loans = loanRepository.findAll();
        assertThat(loans).extracting(Loan::getId)
                .contains(loan1.getId(), loan2.getId());
    }

    @Test
    void updateLoanStatus() {
        Customer customer = Customer.builder()
                .firstName("Carlos")
                .lastName("Sanchez")
                .secondLastName("Lopez")
                .dateOfBirth(LocalDate.of(1988, 2, 20))
                .build();
        entityManager.persistAndFlush(customer);

        CreditLine creditLine = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("12000.00"))
                .availableCreditAmount(new BigDecimal("12000.00"))
                .status(CreditLineStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(creditLine);

        Loan loan = Loan.builder()
                .creditLine(creditLine)
                .amount(new BigDecimal("4000.00"))
                .totalAmount(new BigDecimal("4400.00"))
                .status(LoanStatus.ACTIVE)
                .paymentScheme(PaymentScheme.SCHEME_2)
                .interestRate(new BigDecimal("4.00"))
                .commissionAmount(new BigDecimal("80.00"))
                .purchaseDate(LocalDate.of(2024, 5, 1))
                .build();
        entityManager.persistAndFlush(loan);

        loan.setStatus(LoanStatus.COMPLETED);
        loanRepository.save(loan);
        entityManager.flush();
        entityManager.clear();

        Optional<Loan> updatedLoan = loanRepository.findById(loan.getId());
        assertThat(updatedLoan).isPresent();
        assertThat(updatedLoan.get().getStatus()).isEqualTo(LoanStatus.COMPLETED);
    }

    @Test
    void cascadePersistInstallments() {
        Customer customer = Customer.builder()
                .firstName("Laura")
                .lastName("Gomez")
                .secondLastName("Martinez")
                .dateOfBirth(LocalDate.of(1992, 7, 10))
                .build();
        entityManager.persistAndFlush(customer);

        CreditLine creditLine = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("20000.00"))
                .availableCreditAmount(new BigDecimal("20000.00"))
                .status(CreditLineStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(creditLine);

        Loan loan = Loan.builder()
                .creditLine(creditLine)
                .amount(new BigDecimal("8000.00"))
                .totalAmount(new BigDecimal("8800.00"))
                .status(LoanStatus.ACTIVE)
                .paymentScheme(PaymentScheme.SCHEME_2)
                .interestRate(new BigDecimal("4.75"))
                .commissionAmount(new BigDecimal("120.00"))
                .purchaseDate(LocalDate.of(2024, 6, 1))
                .build();

        Installment installment1 = Installment.builder()
                .loan(loan)
                .amount(new BigDecimal("2000.00"))
                .scheduledPaymentDate(LocalDate.of(2024, 7, 1))
                .status(InstallmentStatus.PENDING)
                .installmentNumber(1)
                .build();

        Installment installment2 = Installment.builder()
                .loan(loan)
                .amount(new BigDecimal("2000.00"))
                .scheduledPaymentDate(LocalDate.of(2024, 8, 1))
                .status(InstallmentStatus.PENDING)
                .installmentNumber(2)
                .build();

        loan.getInstallments().add(installment1);
        loan.getInstallments().add(installment2);

        entityManager.persistAndFlush(loan);
        entityManager.clear();

        Optional<Loan> foundLoan = loanRepository.findById(loan.getId());
        assertThat(foundLoan).isPresent();
        assertThat(foundLoan.get().getInstallments()).hasSize(2);
    }

    @Test
    void deleteLoanAndCascadeDeleteInstallments() {
        Customer customer = Customer.builder()
                .firstName("Mariana")
                .lastName("Ruiz")
                .secondLastName("Fernandez")
                .dateOfBirth(LocalDate.of(1995, 11, 5))
                .build();
        entityManager.persistAndFlush(customer);

        CreditLine creditLine = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("18000.00"))
                .availableCreditAmount(new BigDecimal("18000.00"))
                .status(CreditLineStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(creditLine);

        Loan loan = Loan.builder()
                .creditLine(creditLine)
                .amount(new BigDecimal("6000.00"))
                .totalAmount(new BigDecimal("6600.00"))
                .status(LoanStatus.ACTIVE)
                .paymentScheme(PaymentScheme.SCHEME_1)
                .interestRate(new BigDecimal("4.25"))
                .commissionAmount(new BigDecimal("90.00"))
                .purchaseDate(LocalDate.of(2024, 7, 15))
                .build();

        Installment installment1 = Installment.builder()
                .loan(loan)
                .amount(new BigDecimal("1500.00"))
                .scheduledPaymentDate(LocalDate.of(2024, 8, 15))
                .status(InstallmentStatus.PENDING)
                .installmentNumber(1)
                .build();

        Installment installment2 = Installment.builder()
                .loan(loan)
                .amount(new BigDecimal("1500.00"))
                .scheduledPaymentDate(LocalDate.of(2024, 9, 15))
                .status(InstallmentStatus.PENDING)
                .installmentNumber(2)
                .build();

        loan.getInstallments().add(installment1);
        loan.getInstallments().add(installment2);

        entityManager.persistAndFlush(loan);
        UUID loanId = loan.getId();

        loanRepository.delete(loan);
        entityManager.flush();
        entityManager.clear();

        Optional<Loan> deletedLoan = loanRepository.findById(loanId);
        assertThat(deletedLoan).isNotPresent();

        List<Installment> installments = entityManager.getEntityManager()
                .createQuery("SELECT i FROM Installment i WHERE i.loan.id = :loanId", Installment.class)
                .setParameter("loanId", loanId)
                .getResultList();
        assertThat(installments).isEmpty();
    }
}
