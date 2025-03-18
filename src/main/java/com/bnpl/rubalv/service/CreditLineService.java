package com.bnpl.rubalv.service;

import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;

import java.math.BigDecimal;

public interface CreditLineService {
    CreditLine createCreditLine(Customer customer);
    CreditLine getCustomerCreditLine(Customer customer);
    void updateCreditLine(CreditLine creditLine, BigDecimal amount);
}
