package com.edgarrt.poc.paymentprocessing.infrastructure.config;

import com.edgarrt.poc.paymentprocessing.domain.service.PaymentRiskPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
public class RiskConfiguration {
    @Bean
    PaymentRiskPolicy paymentRiskPolicy(
            @Value("${poc.risk.highAmountThreshold}") BigDecimal highAmountThreshold,
            @Value("${poc.risk.sharedInstrumentThreshold}") long sharedInstrumentThreshold,
            @Value("${poc.risk.failedPaymentThreshold}") long failedPaymentThreshold) {
        return new PaymentRiskPolicy(highAmountThreshold, sharedInstrumentThreshold, failedPaymentThreshold);
    }
}
