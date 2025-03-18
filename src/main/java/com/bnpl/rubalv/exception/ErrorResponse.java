package com.bnpl.rubalv.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ErrorResponse(@Schema(pattern = "^APZ\\d{6}$") String code, String error, String message, Long timestamp, String path) {
}