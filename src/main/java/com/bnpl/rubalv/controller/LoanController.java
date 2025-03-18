package com.bnpl.rubalv.controller;

import com.bnpl.rubalv.dto.request.LoanRequestDto;
import com.bnpl.rubalv.exception.ErrorResponse;
import com.bnpl.rubalv.dto.response.LoanResponseDto;
import com.bnpl.rubalv.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Manage loans")
@SecurityRequirement(name = "aplazoAuth")
public class LoanController {
    private final LoanService loanService;

    @Operation(
            summary = "Create a loan",
            operationId = "createLoan",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Loan request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoanRequestDto.class),
                            examples = @ExampleObject(
                                    name = "Simple request",
                                    description = "Minimal request",
                                    value = """
                                        {
                                            "customerId": "b2863d62-0746-4b26-a6e3-edcb4b9578f2",
                                            "amount": 400.80
                                        }
                                    """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanResponseDto.class)),
                    headers = {
                            @Header(
                                    name = "Location",
                                    description = "Relative path to search for newly created loan",
                                    schema = @Schema(type = "string", format = "uri-reference", example = "/v1/loans/3fa85f64-5717-4562-b3fc-2c963f66afa7")
                            )
                    }
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Invalid request response",
                                    value = """
                                        {
                                          "code": "APZ000006",
                                          "error": "INVALID_LOAN_REQUEST",
                                          "timestamp": 1739397485,
                                          "message": "Error detail",
                                          "path": "/v1/loans"
                                        }"""
                                    )
                    )
            ), @ApiResponse(
                    responseCode = "401",
                    ref = "#/components/responses/UnauthorizedRequest"
            ),
            @ApiResponse(
                    responseCode = "500",
                    ref = "#/components/responses/InternalServerErrorResponse"
            )
    })
    @PostMapping
    public ResponseEntity<LoanResponseDto> createLoan(
            @Valid
            @RequestBody LoanRequestDto loanRequestDto
    ){
        LoanResponseDto response = loanService.registerLoan(loanRequestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/v1/loans/{loanId}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(
            summary = "Get loan identified by loanId",
            operationId = "getLoanById",
            parameters = @Parameter(
                    name = "loanId",
                    in = ParameterIn.PATH,
                    description = "Loan's unique identifier",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ok",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoanResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    ref = "#/components/responses/InvalidRequest"
            ),
            @ApiResponse(
                    responseCode = "401",
                    ref = "#/components/responses/UnauthorizedRequest"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Loan not found",
                                    value = """
                        {
                          "code": "APZ000008",
                          "error": "LOAN_NOT_FOUND",
                          "timestamp": 1739397485,
                          "message": "Error detail",
                          "path": "/v1/loans/3fa85e68-5717-4562-b3fc-2c963f66afa6"
                        }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    ref = "#/components/responses/InternalServerErrorResponse"
            )
    })
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable("loanId") UUID loanId){
        LoanResponseDto response = loanService.getLoanById(loanId);
        return ResponseEntity.ok(response);
    }
}
