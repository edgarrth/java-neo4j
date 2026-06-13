package com.edgarrt.poc.paymentprocessing.domain.model;

import java.util.List;

public record RiskAnalysis(String entityId, RiskLevel riskLevel, List<String> reasons, long sharedInstrumentCustomers, long failedPayments) {}
