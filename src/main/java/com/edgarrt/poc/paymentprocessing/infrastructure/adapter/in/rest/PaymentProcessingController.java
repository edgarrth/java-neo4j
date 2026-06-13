package com.edgarrt.poc.paymentprocessing.infrastructure.adapter.in.rest;

import com.edgarrt.poc.paymentprocessing.application.port.in.*;
import com.edgarrt.poc.paymentprocessing.domain.model.*;
import com.edgarrt.poc.paymentprocessing.infrastructure.adapter.in.rest.request.*;
import com.edgarrt.poc.paymentprocessing.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments/v1")
public class PaymentProcessingController {
    private final RegisterCustomerUseCase registerCustomer;
    private final RegisterMerchantUseCase registerMerchant;
    private final RegisterPaymentInstrumentUseCase registerInstrument;
    private final AuthorizePaymentUseCase authorizePayment;
    private final QueryPaymentUseCase queryPayment;
    private final AnalyzeRiskUseCase analyzeRisk;

    public PaymentProcessingController(RegisterCustomerUseCase registerCustomer, RegisterMerchantUseCase registerMerchant,
                                       RegisterPaymentInstrumentUseCase registerInstrument, AuthorizePaymentUseCase authorizePayment,
                                       QueryPaymentUseCase queryPayment, AnalyzeRiskUseCase analyzeRisk) {
        this.registerCustomer = registerCustomer;
        this.registerMerchant = registerMerchant;
        this.registerInstrument = registerInstrument;
        this.authorizePayment = authorizePayment;
        this.queryPayment = queryPayment;
        this.analyzeRisk = analyzeRisk;
    }

    @PostMapping("/customers")
    public ResponseEntity<ApiResponse<Customer>> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(registerCustomer.register(new Customer(request.customerId(), request.fullName(), request.documentNumber(), request.segment()))));
    }

    @PostMapping("/merchants")
    public ResponseEntity<ApiResponse<Merchant>> createMerchant(@Valid @RequestBody CreateMerchantRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(registerMerchant.register(new Merchant(request.merchantId(), request.legalName(), request.mcc(), request.country()))));
    }

    @PostMapping("/instruments")
    public ResponseEntity<ApiResponse<PaymentInstrument>> createInstrument(@Valid @RequestBody CreateInstrumentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(registerInstrument.register(new PaymentInstrument(request.instrumentId(), request.type(), request.fingerprint(), request.brand(), request.last4()))));
    }

    @PostMapping("/payments/authorize")
    public ResponseEntity<ApiResponse<Payment>> authorize(@Valid @RequestBody AuthorizePaymentRequest request) {
        var command = new AuthorizePaymentUseCase.Command(request.paymentId(), request.customerId(), request.merchantId(), request.instrumentId(), request.amount(), request.currency(), request.channel());
        return ResponseEntity.ok(ApiResponse.ok(authorizePayment.authorize(command)));
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<ApiResponse<Payment>> findPayment(@PathVariable String paymentId) {
        return queryPayment.findById(paymentId).map(p -> ResponseEntity.ok(ApiResponse.ok(p))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/payments/{paymentId}/risk-analysis")
    public ResponseEntity<ApiResponse<RiskAnalysis>> riskAnalysis(@PathVariable String paymentId) {
        return ResponseEntity.ok(ApiResponse.ok(analyzeRisk.analyzePayment(paymentId)));
    }

    @GetMapping("/customers/{customerId}/risk-network")
    public ResponseEntity<ApiResponse<RiskNetwork>> customerRiskNetwork(@PathVariable String customerId) {
        return ResponseEntity.ok(ApiResponse.ok(analyzeRisk.customerRiskNetwork(customerId)));
    }
}
