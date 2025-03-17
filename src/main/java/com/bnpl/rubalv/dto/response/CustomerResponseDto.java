package com.bnpl.rubalv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerResponseDto {
    private UUID id;
    private BigDecimal creditLineAmount;
    private BigDecimal availableCreditLineAmount;
    private OffsetDateTime createdAt;
}
