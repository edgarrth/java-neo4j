package com.edgarrt.poc.paymentprocessing.domain.service;

import com.edgarrt.poc.paymentprocessing.domain.model.RiskLevel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentRiskPolicy {
    private final BigDecimal highAmountThreshold;
    private final long sharedInstrumentThreshold;
    private final long failedPaymentThreshold;

    public PaymentRiskPolicy(BigDecimal highAmountThreshold, long sharedInstrumentThreshold, long failedPaymentThreshold) {
        this.highAmountThreshold = highAmountThreshold;
        this.sharedInstrumentThreshold = sharedInstrumentThreshold;
        this.failedPaymentThreshold = failedPaymentThreshold;
    }

    public Decision decide(BigDecimal amount, long sharedInstrumentCustomers, long failedPayments) {
        List<String> reasons = new ArrayList<>();
        RiskLevel level = RiskLevel.LOW;

        if (amount.compareTo(highAmountThreshold) >= 0) {
            level = RiskLevel.HIGH;
            reasons.add("Amount is greater than or equal to high amount threshold");
        }
        if (sharedInstrumentCustomers >= sharedInstrumentThreshold) {
            level = RiskLevel.HIGH;
            reasons.add("Payment instrument is shared by multiple customers");
        }
        if (failedPayments > failedPaymentThreshold) {
            level = level == RiskLevel.HIGH ? RiskLevel.HIGH : RiskLevel.MEDIUM;
            reasons.add("Customer has repeated declined payments");
        }
        if (reasons.isEmpty()) reasons.add("No relevant graph risk signals found");
        return new Decision(level, reasons);
    }

    public record Decision(RiskLevel riskLevel, List<String> reasons) {}
}
