package com.bnpl.rubalv.utils.helper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.bnpl.rubalv.enums.PaymentScheme;
import com.bnpl.rubalv.utils.helpers.DateHelper;
import org.junit.jupiter.api.Test;

public class DateHelperTest {
    private final DateHelper dateHelper = new DateHelper();

    @Test
    void testCalculateAge_Typical() {
        LocalDate birthDate = LocalDate.now().minusYears(25);
        int age = dateHelper.calculateAge(birthDate);
        assertThat(age).isEqualTo(25);
    }

    @Test
    void testCalculateAge_Today() {
        LocalDate birthDate = LocalDate.now();
        int age = dateHelper.calculateAge(birthDate);
        assertThat(age).isEqualTo(0);
    }

    @Test
    void testCalculateAge_Future() {
        LocalDate birthDate = LocalDate.now().plusYears(1);
        int age = dateHelper.calculateAge(birthDate);
        assertThat(age).isEqualTo(-1);
    }

    @Test
    void testCalculateAge_NullBirthDate_ThrowsException() {
        assertThrows(NullPointerException.class, () -> dateHelper.calculateAge(null));
    }

    @Test
    void testGeneratePaymentSchedule_WithScheme1() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        List<LocalDate> schedule = dateHelper.generatePaymentSchedule(startDate, PaymentScheme.SCHEME_1);

        assertThat(schedule).hasSize(5);
        for (int i = 1; i <= PaymentScheme.SCHEME_1.getNumberOfPayments(); i++) {
            assertThat(schedule.get(i - 1)).isEqualTo(startDate.plusWeeks(2L * i));
        }
    }

    @Test
    void testGeneratePaymentSchedule_WithScheme2() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        List<LocalDate> schedule = dateHelper.generatePaymentSchedule(startDate, PaymentScheme.SCHEME_2);

        assertThat(schedule).hasSize(5);
        for (int i = 1; i <= PaymentScheme.SCHEME_2.getNumberOfPayments(); i++) {
            assertThat(schedule.get(i - 1)).isEqualTo(startDate.plusWeeks(2L * i));
        }
    }

    @Test
    void testGeneratePaymentSchedule_NullStartDate_ThrowsException() {
        assertThrows(NullPointerException.class, () -> dateHelper.generatePaymentSchedule(null, PaymentScheme.SCHEME_1));
    }

    @Test
    void testGeneratePaymentSchedule_NullScheme_ThrowsException() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        assertThrows(NullPointerException.class, () -> dateHelper.generatePaymentSchedule(startDate, null));
    }

}
