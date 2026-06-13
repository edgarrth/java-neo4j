package com.edgarrt.poc.paymentprocessing.application.port.out;

import com.edgarrt.poc.paymentprocessing.domain.model.*;
import java.util.Optional;

public interface PaymentGraphRepository {
    Customer saveCustomer(Customer customer);
    Merchant saveMerchant(Merchant merchant);
    PaymentInstrument saveInstrument(PaymentInstrument instrument);
    Payment savePayment(Payment payment);
    Optional<Payment> findPaymentById(String paymentId);
    RiskAnalysis analyzePaymentRisk(String paymentId);
    RiskNetwork findCustomerRiskNetwork(String customerId);
}
