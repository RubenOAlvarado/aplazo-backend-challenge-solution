package com.bnpl.rubalv.controller;

import com.bnpl.rubalv.dto.request.CreateCustomerRequestDto;
import com.bnpl.rubalv.dto.response.CustomerResponseDto;
import com.bnpl.rubalv.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @Operation(summary = "Create a new customer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer successfully created.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDto.class))
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            ), @ApiResponse(
                    responseCode = "500",
                    description = "Something went wrong"
            )
    })
    @PostMapping("/")
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer to be registered.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateCustomerRequestDto.class)
                    )
            )
            @RequestBody CreateCustomerRequestDto createCustomerRequestDto
    ){
        CustomerResponseDto response = customerService.registerCustomer(createCustomerRequestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Search customer by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer successfully found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponseDto.class))
            ), @ApiResponse(
                        responseCode = "404",
                        description = "Customer not found"
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @Parameter(description = "Customer id to be searched")
            @PathVariable("id") UUID id
    ){
        CustomerResponseDto customerResponseDto = customerService.getCustomerById(id);
        return ResponseEntity.ok(customerResponseDto);
    }
}
