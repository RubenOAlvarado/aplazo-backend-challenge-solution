package com.bnpl.rubalv.utils.helpers;

import com.bnpl.rubalv.enums.PaymentScheme;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DateHelper {
    public int calculateAge(LocalDate birthDate){
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public List<LocalDate> generatePaymentSchedule(LocalDate startDate, PaymentScheme scheme) {
        return IntStream.rangeClosed(1, scheme.getNumberOfPayments())
                .mapToObj(i -> startDate.plusWeeks(2L * i))
                .collect(Collectors.toList());
    }
}
