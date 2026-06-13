package com.edgarrt.poc.paymentprocessing.infrastructure.adapter.in.rest.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record AuthorizePaymentRequest(
        @NotBlank String paymentId,
        @NotBlank String customerId,
        @NotBlank String merchantId,
        @NotBlank String instrumentId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String currency,
        @NotBlank String channel
) {}
