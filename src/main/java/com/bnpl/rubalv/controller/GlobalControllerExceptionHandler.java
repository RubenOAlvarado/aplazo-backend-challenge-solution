package com.bnpl.rubalv.controller;

import com.bnpl.rubalv.dto.response.CustomErrorResponse;
import com.bnpl.rubalv.exception.InsufficientCreditException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex){
        log.error("Data input validation: {}", ex.getMessage(), ex);
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": "+error.getDefaultMessage())
                .toList();

        return CustomErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .type("VALIDATION_ERROR")
                .message(errors.toString())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorResponse handleIllegalArgumentException(IllegalArgumentException ex){
        log.error("Invalid input: {}", ex.getMessage(), ex);
        return CustomErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .type("INVALID_INPUT")
                .message(ex.getMessage())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @ExceptionHandler(InsufficientCreditException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorResponse handleInsufficientCredit(InsufficientCreditException ex) {
        log.error("Customer does not have enough credit: {}", ex.getMessage(), ex);
        return CustomErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .type("INSUFFICIENT_CREDIT")
                .message(ex.getMessage())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomErrorResponse handleEntityNotFound(EntityNotFoundException ex) {
        log.error("Entity not found: {}", ex.getMessage(), ex);
        return CustomErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .type("ENTITY_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomErrorResponse handleGenericException(Exception ex) {
        return CustomErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .type("INTERNAL_ERROR")
                .message("Something went wrong")
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
