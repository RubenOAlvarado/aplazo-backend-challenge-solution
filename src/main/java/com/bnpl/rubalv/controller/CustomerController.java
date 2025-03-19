package com.bnpl.rubalv.controller;

import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import com.bnpl.rubalv.exception.ErrorResponse;
import com.bnpl.rubalv.dto.response.CustomerRegistrationResult;
import com.bnpl.rubalv.dto.response.CustomerResponseDto;
import com.bnpl.rubalv.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.links.Link;
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
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Manage customers")
public class CustomerController {
    private final CustomerService customerService;

    @Operation(
            summary = "Create a customer",
            operationId = "createCustomer",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateCustomerRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Customer under 18",
                                            description = "Under age customer",
                                            value = """
                            {
                              "firstName": "Juan",
                              "lastName": "López",
                              "secondLastName": "Pérez",
                              "dateOfBirth": "2009-11-02"
                            }"""
                                    ),
                                    @ExampleObject(
                                            name = "Customer accepted",
                                            description = "Customer which age is in accepted range",
                                            value = """
                            {
                              "firstName": "Pepe",
                              "lastName": "García",
                              "secondLastName": "Flores",
                              "dateOfBirth": "1998-07-21"
                            }"""
                                    )
                            }
                    ),
                    required = true
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    headers = {
                            @Header(
                                    name = "Location",
                                    description = "Relative path to search for newly created customer",
                                    schema = @Schema(type = "string", format = "uri-reference", example = "/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa7")
                            ),
                            @Header(
                                    name = "X-Auth-Token",
                                    description = "JWT with the required roles and required for authentication",
                                    schema = @Schema(type = "string", format = "base64", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            )
                    },
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDto.class)
                    ),
                    links = @Link(
                            name = "GetCustomerById",
                            operationId = "getCustomerById",
                            description = "The `id` value returned can be used as `customerId` in GET /customers/{customerId}"
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Invalid request response",
                                    value = """
                                {
                                  "code": "APZ000002",
                                  "error": "INVALID_CUSTOMER_REQUEST",
                                  "timestamp": 1739397485,
                                  "message": "Error detail",
                                  "path": "/v1/customers"
                                }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Too many request response",
                                    value = """
                                    {
                                      "code": "APZ000003",
                                      "error": "RATE_LIMIT_ERROR",
                                      "timestamp": 1739397485,
                                      "message": "Error detail",
                                      "path": "/v1/customers"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    ref = "#/components/responses/InternalServerErrorResponse"
            )
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @Valid
            @RequestBody CreateCustomerRequestDto createCustomerRequestDto
    ){
        CustomerRegistrationResult result = customerService.registerCustomer(createCustomerRequestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/v1/customers/{customerId}")
                .buildAndExpand(result.getCustomer().getId())
                .toUri();

        return ResponseEntity.created(location)
                .header("X-Auth-Token", result.getToken())
                .body(result.getCustomer());
    }

    @Operation(
            summary = "Get customer identified by `customerId`",
            operationId = "getCustomerById",
            parameters = @Parameter(
                    name = "customerId",
                    in = ParameterIn.PATH,
                    description = "Customer's unique identifier",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            ),
            security = @SecurityRequirement(name = "aplazoAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ok",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDto.class)
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
                                    name = "Customer not found response",
                                    value = """
                                    {
                                      "code": "APZ000005",
                                      "error": "CUSTOMER_NOT_FOUND",
                                      "timestamp": 1739397485,
                                      "message": "Error detail",
                                      "path": "/v1/customers/{customerId}"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    ref = "#/components/responses/InternalServerErrorResponse"
            )
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @PathVariable("customerId") UUID customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }
}
