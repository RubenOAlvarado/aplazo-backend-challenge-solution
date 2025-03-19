package com.bnpl.rubalv.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response structure")
public class ErrorResponse{
    @Schema(pattern = "^APZ\\d{6}$", description = "Unique error code", example = "APZ000002")
    private String code;

    @Schema(description = "Error type", example = "INVALID_REQUEST")
    private String error;


    @Schema(description = "Human readable message error", example = "Missing or invalid authentication token")
    private String message;

    @Schema(description = "Error timestamp", example = "1739397485")
    private Long timestamp;

    @Schema(description = "Path where the error occurs", example = "/v1/customers")
    private String path;
}