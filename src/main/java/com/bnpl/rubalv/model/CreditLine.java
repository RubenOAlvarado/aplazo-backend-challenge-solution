package com.bnpl.rubalv.model;

import com.bnpl.rubalv.enums.CreditLineStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditLine {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCreditAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal availableCreditAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CreditLineStatus status;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
