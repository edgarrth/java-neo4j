package com.edgarrt.poc.paymentprocessing.infrastructure.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;

public record CreateInstrumentRequest(@NotBlank String instrumentId, @NotBlank String type, @NotBlank String fingerprint, @NotBlank String brand, @NotBlank String last4) {}
