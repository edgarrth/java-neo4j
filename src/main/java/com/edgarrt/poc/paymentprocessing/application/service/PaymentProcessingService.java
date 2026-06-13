package com.edgarrt.poc.paymentprocessing.application.service;

import com.edgarrt.poc.paymentprocessing.application.port.in.*;
import com.edgarrt.poc.paymentprocessing.application.port.out.PaymentGraphRepository;
import com.edgarrt.poc.paymentprocessing.domain.model.*;
import com.edgarrt.poc.paymentprocessing.domain.service.PaymentRiskPolicy;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

@Service
public class PaymentProcessingService implements RegisterCustomerUseCase, RegisterMerchantUseCase,
        RegisterPaymentInstrumentUseCase, AuthorizePaymentUseCase, QueryPaymentUseCase, AnalyzeRiskUseCase {

    private final PaymentGraphRepository repository;
    private final PaymentRiskPolicy riskPolicy;

    public PaymentProcessingService(PaymentGraphRepository repository, PaymentRiskPolicy riskPolicy) {
        this.repository = repository;
        this.riskPolicy = riskPolicy;
    }

    @Override public Customer register(Customer customer) { return repository.saveCustomer(customer); }
    @Override public Merchant register(Merchant merchant) { return repository.saveMerchant(merchant); }
    @Override public PaymentInstrument register(PaymentInstrument instrument) { return repository.saveInstrument(instrument); }

    @Override
    public Payment authorize(Command command) {
        RiskAnalysis graphSignals = repository.analyzePaymentRisk(command.paymentId());
        PaymentRiskPolicy.Decision decision = riskPolicy.decide(command.amount(), graphSignals.sharedInstrumentCustomers(), graphSignals.failedPayments());
        PaymentStatus status = decision.riskLevel() == RiskLevel.HIGH ? PaymentStatus.DECLINED : PaymentStatus.AUTHORIZED;
        Payment payment = new Payment(command.paymentId(), command.customerId(), command.merchantId(), command.instrumentId(),
                command.amount(), command.currency(), command.channel(), status, decision.riskLevel(),
                String.join("; ", decision.reasons()), Instant.now());
        return repository.savePayment(payment);
    }

    @Override public Optional<Payment> findById(String paymentId) { return repository.findPaymentById(paymentId); }
    @Override public RiskAnalysis analyzePayment(String paymentId) { return repository.analyzePaymentRisk(paymentId); }
    @Override public RiskNetwork customerRiskNetwork(String customerId) { return repository.findCustomerRiskNetwork(customerId); }
}
