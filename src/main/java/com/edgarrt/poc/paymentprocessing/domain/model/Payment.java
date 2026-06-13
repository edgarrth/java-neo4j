package com.edgarrt.poc.paymentprocessing.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Payment(
        String paymentId,
        String customerId,
        String merchantId,
        String instrumentId,
        BigDecimal amount,
        String currency,
        String channel,
        PaymentStatus status,
        RiskLevel riskLevel,
        String riskReason,
        Instant createdAt
) {}
