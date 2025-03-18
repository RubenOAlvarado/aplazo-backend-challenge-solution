package com.bnpl.rubalv.controller;

import com.bnpl.rubalv.exception.ClientWithoutCreditLineException;
import com.bnpl.rubalv.exception.ErrorResponse;
import com.bnpl.rubalv.exception.InsufficientCreditException;
import com.bnpl.rubalv.exception.TooManyRequestsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Data input validation: {}", ex.getMessage(), ex);

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ErrorResponse.builder()
                .code("APZ000002")
                .error("INVALID_CUSTOMER_REQUEST")
                .message("Validation errors: " + errors)
                .timestamp(Instant.now().getEpochSecond())
                .path(getRequestPath(request))
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Invalid input: {}", ex.getMessage(), ex);
        return ErrorResponse.builder()
                .code("APZ000004")
                .error("INVALID_REQUEST")
                .message(ex.getMessage())
                .timestamp(Instant.now().getEpochSecond())
                .path(getRequestPath(request))
                .build();
    }

    @ExceptionHandler(InsufficientCreditException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientCredit(InsufficientCreditException ex, WebRequest request) {
        log.error("Insufficient credit: {}", ex.getMessage(), ex);
        return ErrorResponse.builder()
                .code("APZ000009")
                .error("INSUFFICIENT_CREDIT")
                .message(ex.getMessage())
                .timestamp(Instant.now().getEpochSecond())
                .path(getRequestPath(request))
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        log.error("Entity not found: {}", ex.getMessage(), ex);

        String errorCode = "APZ000005";
        String errorType = "CUSTOMER_NOT_FOUND";

        if (request.getDescription(false).contains("/loans/")) {
            errorCode = "APZ000008";
            errorType = "LOAN_NOT_FOUND";
        }

        return ErrorResponse.builder()
                .code(errorCode)
                .error(errorType)
                .message(ex.getMessage())
                .timestamp(Instant.now().getEpochSecond())
                .path(getRequestPath(request))
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex, WebRequest request) {
        log.error("Internal error: {}", ex.getMessage(), ex);
        return ErrorResponse.builder()
                .code("APZ000001")
                .error("INTERNAL_SERVER_ERROR")
                .message("Internal server error")
                .timestamp(Instant.now().getEpochSecond())
                .path(getRequestPath(request))
                .build();
    }

    @ExceptionHandler(TooManyRequestsException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponse handleTooManyRequests(TooManyRequestsException ex, WebRequest request) {
        return ErrorResponse.builder()
                .code("APZ000003")
                .error("RATE_LIMIT_ERROR")
                .message(ex.getMessage())
                .timestamp(Instant.now().getEpochSecond())
                .path(getRequestPath(request))
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return ErrorResponse.builder()
                .code("APZ000007")
                .error("UNAUTHORIZED")
                .message("Missing or invalid authentication token")
                .timestamp(Instant.now().getEpochSecond())
                .path(getRequestPath(request))
                .build();
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConversionErrors(ConversionFailedException ex, WebRequest request) {
        return ErrorResponse.builder()
                .code("APZ000004")
                .error("INVALID_REQUEST")
                .message("Invalid UUID format")
                .timestamp(Instant.now().getEpochSecond())
                .path(getRequestPath(request))
                .build();
    }

    @ExceptionHandler(ClientWithoutCreditLineException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotCreditLine(ClientWithoutCreditLineException ex, WebRequest request){
        return ErrorResponse.builder()
                .code("APZ000004")
                .error("INVALID_REQUEST")
                .message(ex.getMessage())
                .timestamp(Instant.now().getEpochSecond())
                .path(getRequestPath(request))
                .build();
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "unknown";
    }
}
