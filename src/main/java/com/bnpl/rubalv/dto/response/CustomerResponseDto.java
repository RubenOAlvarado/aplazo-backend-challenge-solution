package com.bnpl.rubalv.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {
    @Schema(description = "Customer's unique identifier", format = "uuid")
    private UUID id;

    @Schema(description = "Approved credit line", minimum = "0.01")
    private BigDecimal creditLineAmount;

    @Schema(description = "Available credit", minimum = "0.0")
    private BigDecimal availableCreditLineAmount;

    @Schema(description = "Creation timestamp", format = "date-time")
    private OffsetDateTime createdAt;
}
