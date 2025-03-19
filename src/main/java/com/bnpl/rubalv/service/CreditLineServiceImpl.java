package com.bnpl.rubalv.service;

import com.bnpl.rubalv.constants.CreditLineConstants;
import com.bnpl.rubalv.model.CreditLine;
import com.bnpl.rubalv.model.Customer;
import com.bnpl.rubalv.enums.CreditLineStatus;
import com.bnpl.rubalv.repository.CreditLineRepository;
import com.bnpl.rubalv.utils.helpers.DateHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditLineServiceImpl implements CreditLineService {
    private final CreditLineRepository creditLineRepository;
    private final DateHelper dateHelper;

    @Override
    public CreditLine createCreditLine(Customer customer){
        log.info("Starting credit line creation for customer: {}", customer.getId());
        try {
            int age = dateHelper.calculateAge(customer.getDateOfBirth());
            validateAgeEligibility(age);

            BigDecimal creditLineAmount = determineCreditLineAmount(age);
            log.info("Credit line amount determinate for customer {} : {}", customer.getId(), creditLineAmount);
            CreditLine creditLine = buildCreditLine(customer, creditLineAmount);

            log.info("Credit line successfully created. ID: {}", creditLine.getTotalCreditAmount());
            return creditLineRepository.save(creditLine);
        } catch(IllegalArgumentException e){
            log.error("Customer does not meet credit line criteria {}: {}", customer.getId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public CreditLine getCustomerCreditLine(Customer customer){
        log.debug("Searching credit line with active status for customer: {}", customer.getId());
        Optional<CreditLine> creditLine = creditLineRepository.findByCustomerAndStatusEquals(customer, CreditLineStatus.ACTIVE);

        creditLine.ifPresentOrElse(
                cl -> log.debug("Credit line found: {}", cl.getId()),
                () -> log.warn("An active credit line cannot be found for client: {}", customer.getId())
        );

        return creditLine.orElse(null);
    }

    @Override
    public void updateCreditLine(CreditLine creditLine, BigDecimal amount) {
        log.info("Updating credit line {} - Current credit amount: {} - Amount to subtract: {}", creditLine.getId(), creditLine.getAvailableCreditAmount(), amount);
        BigDecimal newAvailableCredit = creditLine.getAvailableCreditAmount().subtract(amount);
        creditLine.setAvailableCreditAmount(newAvailableCredit);
        CreditLine updatedCreditLine = creditLineRepository.save(creditLine);
        log.debug("Updated credit line, new credit amount: {}", updatedCreditLine.getAvailableCreditAmount());
    }

    private void validateAgeEligibility(int age){
        log.debug("Customer eligibility validation: {}", age);
        if(age < CreditLineConstants.AGE_MINIMUM || age > CreditLineConstants.AGE_MAXIMUM){
            String errMsg = String.format("Client age must be between the range (%d-%d years)", CreditLineConstants.AGE_MINIMUM, CreditLineConstants.AGE_MAXIMUM);
            log.error("Validation error: {}", errMsg);
            throw new IllegalArgumentException(errMsg);
        }
    }

    private BigDecimal determineCreditLineAmount(int age){
        log.debug("Determining customers credit line per age: {}", age);
        BigDecimal creditAmount;

        if(age >= CreditLineConstants.AGE_MINIMUM && age <= CreditLineConstants.YOUNG_ADULT_AGE_LIMIT){
            creditAmount = CreditLineConstants.CREDIT_AMOUNT_YOUNG_ADULT;
            log.trace("Young adult customer, credit amount: {}", creditAmount);
        } else if(age <= CreditLineConstants.ADULT_AGE_LIMIT){
            creditAmount = CreditLineConstants.CREDIT_AMOUNT_ADULT;
            log.trace("Adult customer, credit amount: {}", creditAmount);
        }else {
            creditAmount = CreditLineConstants.CREDIT_AMOUNT_SENIOR;
            log.trace("Senior customer, credit amount: {}", creditAmount);
        }

        return creditAmount;
    }

    private CreditLine buildCreditLine(Customer customer, BigDecimal amount){
        return CreditLine.builder()
                .customer(customer)
                .totalCreditAmount(amount)
                .availableCreditAmount(amount)
                .status(CreditLineStatus.ACTIVE)
                .build();
    }
}
