package com.bnpl.rubalv.exception;

public class InsufficientCreditException extends BussinessException{
    public InsufficientCreditException(String message) {
        super(message);
    }
}
