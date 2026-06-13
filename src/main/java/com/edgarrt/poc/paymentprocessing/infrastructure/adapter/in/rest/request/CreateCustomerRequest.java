package com.edgarrt.poc.paymentprocessing.infrastructure.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(@NotBlank String customerId, @NotBlank String fullName, @NotBlank String documentNumber, @NotBlank String segment) {}
