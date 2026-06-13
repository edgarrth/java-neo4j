package com.edgarrt.poc.paymentprocessing.infrastructure.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;

public record CreateMerchantRequest(@NotBlank String merchantId, @NotBlank String legalName, @NotBlank String mcc, @NotBlank String country) {}
