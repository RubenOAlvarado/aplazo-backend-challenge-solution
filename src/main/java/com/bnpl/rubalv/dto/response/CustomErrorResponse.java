package com.bnpl.rubalv.dto.response;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record CustomErrorResponse(int code, String type, String message, OffsetDateTime timestamp) {
}