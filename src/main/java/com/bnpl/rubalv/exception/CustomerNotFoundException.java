package com.bnpl.rubalv.exception;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class CustomerNotFoundException extends EntityNotFoundException {
    public CustomerNotFoundException(UUID customerId) {
        super("Customer not found with ID: " + customerId);
    }
}
