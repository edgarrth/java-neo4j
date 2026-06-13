package com.edgarrt.poc.paymentprocessing.domain.model;

public record PaymentInstrument(String instrumentId, String type, String fingerprint, String brand, String last4) {}
