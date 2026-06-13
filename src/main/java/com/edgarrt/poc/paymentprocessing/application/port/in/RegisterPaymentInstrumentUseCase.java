package com.edgarrt.poc.paymentprocessing.application.port.in;

import com.edgarrt.poc.paymentprocessing.domain.model.PaymentInstrument;

public interface RegisterPaymentInstrumentUseCase { PaymentInstrument register(PaymentInstrument instrument); }
