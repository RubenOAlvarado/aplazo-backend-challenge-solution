package com.bnpl.rubalv.service;

import com.bnpl.rubalv.dto.request.LoanRequestDto;
import com.bnpl.rubalv.dto.response.LoanResponseDto;

import java.util.UUID;

public interface LoanService {
    LoanResponseDto registerLoan(LoanRequestDto loanRequest);
    LoanResponseDto getLoanById(UUID id);
}
