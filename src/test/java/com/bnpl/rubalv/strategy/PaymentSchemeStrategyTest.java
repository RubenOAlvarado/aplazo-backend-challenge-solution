package com.bnpl.rubalv.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bnpl.rubalv.enums.PaymentScheme;
import com.bnpl.rubalv.model.Customer;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class PaymentSchemeStrategyTest {
    private final PaymentSchemeStrategy strategy = new PaymentSchemeStrategy();

    @ParameterizedTest
    @ValueSource(strings = {"Carlos", "lucia", "HECTOR", "carmen", "Luis", "hannah"})
    void determinePaymentSchema_ValidSchemaOneInitials_ReturnsScheme1(String firstName) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setSequentialId(10L);

        PaymentScheme result = strategy.determinePaymentSchema(customer);

        assertThat(result).isEqualTo(PaymentScheme.SCHEME_1);
    }

    @ParameterizedTest
    @CsvSource({
            "Alice, 30",
            "Bob, 25",
            "David, 0",
            ", 10",
            "Eve, 100"
    })
    void determinePaymentSchema_NonSchemaOneInitials_ReturnsScheme2(String firstName, long sequentialId) {
        Customer customer = new Customer();
        customer.setFirstName(firstName != null ? firstName : "");
        customer.setSequentialId(sequentialId);

        PaymentScheme result = strategy.determinePaymentSchema(customer);

        assertThat(result).isEqualTo(PaymentScheme.SCHEME_2);
    }

    @Test
    public void determinePaymentSchema_NullCustomer_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            strategy.determinePaymentSchema(null);
        });
    }

    @Test
    public void determinePaymentSchema_EmptyFirstName_ReturnsScheme2() {
        Customer customer = new Customer();
        customer.setFirstName("");
        customer.setSequentialId(5L);

        PaymentScheme result = strategy.determinePaymentSchema(customer);

        assertThat(result).isEqualTo(PaymentScheme.SCHEME_2);
    }

    @Test
    public void determinePaymentSchema_FirstNameWithSpecialCharacters_ReturnsScheme2() {
        Customer customer = new Customer();
        customer.setFirstName("123Maria");
        customer.setSequentialId(5L);

        PaymentScheme result = strategy.determinePaymentSchema(customer);

        assertThat(result).isEqualTo(PaymentScheme.SCHEME_2);
    }
}
