package com.edgarrt.poc.paymentprocessing.domain.model;

import java.util.List;

public record RiskNetwork(String customerId, List<String> connectedCustomers, List<String> instruments, List<String> merchants, long totalPayments) {}
