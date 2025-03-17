package com.bnpl.rubalv.model.enums;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public enum PaymentScheme {
    SCHEME_1(5, new BigDecimal("0.13")),
    SCHEME_2(5, new BigDecimal("0.16"));

    private final int numberOfPayments;
    private final BigDecimal interestRate;
}
