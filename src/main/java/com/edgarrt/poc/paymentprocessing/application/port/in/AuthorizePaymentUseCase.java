package com.edgarrt.poc.paymentprocessing.application.port.in;

import com.edgarrt.poc.paymentprocessing.domain.model.Payment;
import java.math.BigDecimal;

public interface AuthorizePaymentUseCase {
    Payment authorize(Command command);
    record Command(String paymentId, String customerId, String merchantId, String instrumentId, BigDecimal amount, String currency, String channel) {}
}
