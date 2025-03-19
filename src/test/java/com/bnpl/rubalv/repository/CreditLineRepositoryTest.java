package com.bnpl.rubalv.repository;

import com.bnpl.rubalv.enums.CreditLineStatus;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
public class CreditLineRepositoryTest {

    @Autowired
    private CreditLineRepository creditLineRepository;

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
    void findActiveByCustomer_WhenActiveCreditLineExists_ReturnsActiveStatusCreditLine() {
        Customer customer = Customer.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .secondLastName("Gómez")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .build();
        entityManager.persistAndFlush(customer);

        CreditLine activeCreditLine = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("10000.00"))
                .availableCreditAmount(new BigDecimal("5000.00"))
                .status(CreditLineStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(activeCreditLine);

        Optional<CreditLine> result = creditLineRepository.findByCustomerAndStatusEquals(customer, CreditLineStatus.ACTIVE);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(activeCreditLine.getId());
        assertThat(result.get().getStatus()).isEqualTo(CreditLineStatus.ACTIVE);
    }

    @Test
    void findActiveByCustomer_WhenMultipleCreditLines_ReturnsOnlyActiveStatus() {
        Customer customer = Customer.builder()
                .firstName("Luis")
                .lastName("Guzman")
                .secondLastName("Gómez")
                .dateOfBirth(LocalDate.of(1986, 6, 16))
                .build();
        entityManager.persistAndFlush(customer);

        CreditLine active = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("20000.00"))
                .availableCreditAmount(new BigDecimal("10000.00"))
                .status(CreditLineStatus.ACTIVE)
                .build();
        entityManager.persistAndFlush(active);

        CreditLine inactive = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("5000.00"))
                .availableCreditAmount(BigDecimal.ZERO)
                .status(CreditLineStatus.CLOSED)
                .build();
        entityManager.persistAndFlush(inactive);

        Optional<CreditLine> result = creditLineRepository.findByCustomerAndStatusEquals(customer, CreditLineStatus.ACTIVE);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(active.getId());
    }

    @Test
    void findActiveByCustomer_WhenNoActiveStatusCreditLine_ReturnsEmpty() {
        Customer customer = Customer.builder()
                .firstName("Luis")
                .lastName("Gatica")
                .secondLastName("Gómez")
                .dateOfBirth(LocalDate.of(1988, 8, 18))
                .build();
        entityManager.persistAndFlush(customer);

        CreditLine inactive = CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(new BigDecimal("3000.00"))
                .availableCreditAmount(BigDecimal.ZERO)
                .status(CreditLineStatus.SUSPENDED)
                .build();
        entityManager.persistAndFlush(inactive);

        Optional<CreditLine> result = creditLineRepository.findByCustomerAndStatusEquals(customer, CreditLineStatus.ACTIVE);

        assertThat(result).isEmpty();
    }
}
