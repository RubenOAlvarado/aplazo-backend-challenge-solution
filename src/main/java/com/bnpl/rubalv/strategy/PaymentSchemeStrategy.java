package com.bnpl.rubalv.strategy;

import com.bnpl.rubalv.enums.PaymentScheme;
import com.bnpl.rubalv.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSchemeStrategy {
    private static final Set<Character> SCHEME_ONE_INITIALS = Set.of('C', 'L', 'H');

    public PaymentScheme determinePaymentSchema(Customer customer){
        log.info("Determining payment schema");
        if(isSchemaOneApplicable(customer)){
            log.info("Customer is applicable for schema one");
            return PaymentScheme.SCHEME_1;
        }else if(customer.getSequentialId() > 25){
            log.info("Customer is applicable for schema two");
            return PaymentScheme.SCHEME_2;
        }

        log.info("Customer is applicable for default schema");
        return PaymentScheme.SCHEME_2;
    }

    private boolean isSchemaOneApplicable(Customer customer){
        log.info("Checking if customer is applicable for schema one");
        return !customer.getFirstName().isEmpty() &&
                SCHEME_ONE_INITIALS.contains(Character.toUpperCase(customer.getFirstName().charAt(0)));
    }
}
