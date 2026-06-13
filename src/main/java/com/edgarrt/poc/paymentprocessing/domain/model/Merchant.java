package com.edgarrt.poc.paymentprocessing.domain.model;

public record Merchant(String merchantId, String legalName, String mcc, String country) {}
