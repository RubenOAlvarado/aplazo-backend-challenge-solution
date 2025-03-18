package com.bnpl.rubalv.exception;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class LoanNotFoundException extends EntityNotFoundException {
    public LoanNotFoundException(UUID loanId) {
        super("Loan not found with ID: " + loanId);
    }
}
