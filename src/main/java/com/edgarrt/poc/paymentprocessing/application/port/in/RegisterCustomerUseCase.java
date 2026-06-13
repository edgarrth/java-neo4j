package com.edgarrt.poc.paymentprocessing.application.port.in;

import com.edgarrt.poc.paymentprocessing.domain.model.Customer;

public interface RegisterCustomerUseCase { Customer register(Customer customer); }
