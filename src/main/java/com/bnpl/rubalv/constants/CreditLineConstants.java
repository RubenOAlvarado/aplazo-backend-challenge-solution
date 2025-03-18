package com.bnpl.rubalv.constants;

import java.math.BigDecimal;

public final class CreditLineConstants {
    private CreditLineConstants(){}

    public static final int AGE_MINIMUM = 18;
    public static final int AGE_MAXIMUM = 65;
    public static final int YOUNG_ADULT_AGE_LIMIT = 25;
    public static final int ADULT_AGE_LIMIT = 30;
    public static final BigDecimal CREDIT_AMOUNT_YOUNG_ADULT = new BigDecimal("3000.00");
    public static final BigDecimal CREDIT_AMOUNT_ADULT = new BigDecimal("5000.00");
    public static final BigDecimal CREDIT_AMOUNT_SENIOR = new BigDecimal("8000.00");
}
