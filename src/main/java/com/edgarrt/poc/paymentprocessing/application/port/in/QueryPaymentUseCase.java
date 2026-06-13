package com.edgarrt.poc.paymentprocessing.application.port.in;

import com.edgarrt.poc.paymentprocessing.domain.model.Payment;
import java.util.Optional;

public interface QueryPaymentUseCase { Optional<Payment> findById(String paymentId); }
