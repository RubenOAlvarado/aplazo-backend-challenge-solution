package com.bnpl.rubalv.repository;

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

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
public class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

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

    private Customer createTestCustomer() {
        return Customer.builder()
                .firstName("Ana")
                .lastName("García")
                .secondLastName("López")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void findBySequentialId_WhenCustomerExists_ReturnsCustomer() {
        Customer customer = createTestCustomer();
        entityManager.persistAndFlush(customer);
        entityManager.refresh(customer);

        Optional<Customer> result = customerRepository.findBySequentialId(customer.getSequentialId());

        assertThat(result)
                .isPresent()
                .get()
                .extracting(
                        Customer::getFirstName,
                        Customer::getLastName,
                        Customer::getSequentialId
                ).containsExactly(
                        "Ana",
                        "García",
                        customer.getSequentialId()
                );
    }

    @Test
    void findBySequentialId_WithMultipleCustomers_ReturnsCorrectOne() {
        Customer customer1 = createTestCustomer();
        entityManager.persistAndFlush(customer1);
        entityManager.refresh(customer1);

        Customer customer2 = Customer.builder()
                .firstName("Pedro")
                .lastName("Martínez")
                .secondLastName("Sánchez")
                .dateOfBirth(LocalDate.of(1980, 5, 20))
                .build();
        entityManager.persistAndFlush(customer2);
        entityManager.refresh(customer2);

        Optional<Customer> result = customerRepository.findBySequentialId(customer1.getSequentialId());

        assertThat(result)
                .isPresent()
                .get()
                .extracting(Customer::getSequentialId)
                .isEqualTo(customer1.getSequentialId());
    }

    @Test
    void findBySequentialId_WhenNotExists_ReturnsEmpty() {
        Optional<Customer> result = customerRepository.findBySequentialId(999L); // ID inexistente
        assertThat(result).isEmpty();
    }

    @Test
    void findBySequentialId_WithNullId_ReturnsEmpty() {
        Optional<Customer> result = customerRepository.findBySequentialId(null);
        assertThat(result).isEmpty();
    }
}
