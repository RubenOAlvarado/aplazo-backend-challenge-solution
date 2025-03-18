package com.bnpl.rubalv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
public enum PaymentScheme {
    SCHEME_1(5, new BigDecimal("0.13")),
    SCHEME_2(5, new BigDecimal("0.16"));

    private final int numberOfPayments;
    private final BigDecimal interestRate;
}
