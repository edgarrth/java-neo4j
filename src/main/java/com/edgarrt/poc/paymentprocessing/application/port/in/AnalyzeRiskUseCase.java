package com.edgarrt.poc.paymentprocessing.application.port.in;

import com.edgarrt.poc.paymentprocessing.domain.model.RiskAnalysis;
import com.edgarrt.poc.paymentprocessing.domain.model.RiskNetwork;

public interface AnalyzeRiskUseCase {
    RiskAnalysis analyzePayment(String paymentId);
    RiskNetwork customerRiskNetwork(String customerId);
}
