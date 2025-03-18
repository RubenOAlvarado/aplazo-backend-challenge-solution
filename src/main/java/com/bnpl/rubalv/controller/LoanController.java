package com.bnpl.rubalv.controller;

import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import com.bnpl.rubalv.dto.request.LoanRequestDto;
import com.bnpl.rubalv.dto.response.CustomerResponseDto;
import com.bnpl.rubalv.dto.response.LoanResponseDto;
import com.bnpl.rubalv.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @Operation(summary = "Create a new customer loan")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Loan successfully created.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanRequestDto.class))
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            ), @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong"
            )
    })
    @PostMapping("/")
    public ResponseEntity<LoanResponseDto> createLoan(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Loan to be registered.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoanRequestDto.class)
                    )
            )
            @RequestBody LoanRequestDto loanRequestDto
    ){
        LoanResponseDto response = loanService.registerLoan(loanRequestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Search loan by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan successfully found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponseDto.class))
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Loan not found"
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDto> getLoanById(
            @Parameter(description = "Loan id to be searched")
            @PathVariable("id") UUID id
    ){
        LoanResponseDto response = loanService.getLoanById(id);
        return ResponseEntity.ok(response);
    }
}
