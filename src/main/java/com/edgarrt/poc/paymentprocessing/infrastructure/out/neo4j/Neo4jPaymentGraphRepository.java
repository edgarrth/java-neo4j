package com.edgarrt.poc.paymentprocessing.infrastructure.out.neo4j;

import com.edgarrt.poc.paymentprocessing.application.port.out.PaymentGraphRepository;
import com.edgarrt.poc.paymentprocessing.domain.model.*;
import org.neo4j.driver.Record;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Repository
public class Neo4jPaymentGraphRepository implements PaymentGraphRepository {
    private final Neo4jClient neo4jClient;

    public Neo4jPaymentGraphRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        neo4jClient.query("""
                MERGE (c:Customer {customerId:$customerId})
                SET c.fullName=$fullName, c.documentNumber=$documentNumber, c.segment=$segment
                RETURN c.customerId AS customerId
                """).bindAll(Map.of(
                "customerId", customer.customerId(), "fullName", customer.fullName(),
                "documentNumber", customer.documentNumber(), "segment", customer.segment()
        )).run();
        return customer;
    }

    @Override
    public Merchant saveMerchant(Merchant merchant) {
        neo4jClient.query("""
                MERGE (m:Merchant {merchantId:$merchantId})
                SET m.legalName=$legalName, m.mcc=$mcc, m.country=$country
                """).bindAll(Map.of("merchantId", merchant.merchantId(), "legalName", merchant.legalName(),
                "mcc", merchant.mcc(), "country", merchant.country())).run();
        return merchant;
    }

    @Override
    public PaymentInstrument saveInstrument(PaymentInstrument instrument) {
        neo4jClient.query("""
                MERGE (i:PaymentInstrument {instrumentId:$instrumentId})
                SET i.type=$type, i.fingerprint=$fingerprint, i.brand=$brand, i.last4=$last4
                """).bindAll(Map.of("instrumentId", instrument.instrumentId(), "type", instrument.type(),
                "fingerprint", instrument.fingerprint(), "brand", instrument.brand(), "last4", instrument.last4())).run();
        return instrument;
    }

    @Override
    public Payment savePayment(Payment payment) {
        neo4jClient.query("""
                MERGE (c:Customer {customerId:$customerId})
                MERGE (m:Merchant {merchantId:$merchantId})
                MERGE (i:PaymentInstrument {instrumentId:$instrumentId})
                MERGE (p:Payment {paymentId:$paymentId})
                SET p.amount=$amount, p.currency=$currency, p.channel=$channel,
                    p.status=$status, p.riskLevel=$riskLevel, p.riskReason=$riskReason, p.createdAt=$createdAt
                MERGE (c)-[:INITIATED]->(p)
                MERGE (p)-[:PAID_TO]->(m)
                MERGE (p)-[:USED_INSTRUMENT]->(i)
                MERGE (c)-[:OWNS_OR_USES]->(i)
                """).bindAll(Map.of(
                "paymentId", payment.paymentId(), "customerId", payment.customerId(),
                "merchantId", payment.merchantId(), "instrumentId", payment.instrumentId(),
                "amount", payment.amount(), "currency", payment.currency(), "channel", payment.channel(),
                "status", payment.status().name(), "riskLevel", payment.riskLevel().name(),
                "riskReason", payment.riskReason(), "createdAt", payment.createdAt().toString()
        )).run();
        return payment;
    }

    @Override
    public Optional<Payment> findPaymentById(String paymentId) {
        return neo4jClient.query("""
                MATCH (c:Customer)-[:INITIATED]->(p:Payment {paymentId:$paymentId})-[:PAID_TO]->(m:Merchant)
                MATCH (p)-[:USED_INSTRUMENT]->(i:PaymentInstrument)
                RETURN p.paymentId AS paymentId, c.customerId AS customerId, m.merchantId AS merchantId,
                       i.instrumentId AS instrumentId, p.amount AS amount, p.currency AS currency,
                       p.channel AS channel, p.status AS status, p.riskLevel AS riskLevel,
                       p.riskReason AS riskReason, p.createdAt AS createdAt
                """).bind(paymentId).to("paymentId").fetchAs(Payment.class).mappedBy((typeSystem, record) -> mapPayment(record)).one();
    }

    @Override
    public RiskAnalysis analyzePaymentRisk(String paymentId) {
        return neo4jClient.query("""
                OPTIONAL MATCH (c:Customer)-[:INITIATED]->(p:Payment {paymentId:$paymentId})-[:USED_INSTRUMENT]->(i:PaymentInstrument)
                OPTIONAL MATCH (other:Customer)-[:OWNS_OR_USES]->(i)
                WITH p, c, collect(DISTINCT other.customerId) AS customers
                OPTIONAL MATCH (c)-[:INITIATED]->(failed:Payment {status:'DECLINED'})
                RETURN coalesce(p.paymentId, $paymentId) AS entityId,
                       size(customers) AS sharedInstrumentCustomers,
                       count(failed) AS failedPayments
                """).bind(paymentId).to("paymentId").fetchAs(RiskAnalysis.class).mappedBy((typeSystem, r) -> {
                    long shared = r.get("sharedInstrumentCustomers").asLong(0);
                    long failed = r.get("failedPayments").asLong(0);
                    List<String> reasons = new ArrayList<>();
                    if (shared >= 3) reasons.add("Instrument shared by " + shared + " customers");
                    if (failed > 2) reasons.add("Customer has " + failed + " declined payments");
                    if (reasons.isEmpty()) reasons.add("No graph signals found for stored payment");
                    RiskLevel level = shared >= 3 ? RiskLevel.HIGH : failed > 2 ? RiskLevel.MEDIUM : RiskLevel.LOW;
                    return new RiskAnalysis(r.get("entityId").asString(), level, reasons, shared, failed);
                }).one().orElse(new RiskAnalysis(paymentId, RiskLevel.LOW, List.of("Payment not found or no graph signals yet"), 0, 0));
    }

    @Override
    public RiskNetwork findCustomerRiskNetwork(String customerId) {
        return neo4jClient.query("""
                MATCH (c:Customer {customerId:$customerId})
                OPTIONAL MATCH (c)-[:OWNS_OR_USES]->(i:PaymentInstrument)<-[:OWNS_OR_USES]-(other:Customer)
                OPTIONAL MATCH (c)-[:INITIATED]->(p:Payment)-[:PAID_TO]->(m:Merchant)
                RETURN c.customerId AS customerId,
                       collect(DISTINCT other.customerId) AS connectedCustomers,
                       collect(DISTINCT i.instrumentId) AS instruments,
                       collect(DISTINCT m.merchantId) AS merchants,
                       count(DISTINCT p) AS totalPayments
                """).bind(customerId).to("customerId").fetchAs(RiskNetwork.class).mappedBy((typeSystem, r) -> new RiskNetwork(
                        r.get("customerId").asString(customerId),
                        r.get("connectedCustomers").asList(v -> v.asString()).stream().filter(v -> !v.equals(customerId)).toList(),
                        r.get("instruments").asList(v -> v.asString()),
                        r.get("merchants").asList(v -> v.asString()),
                        r.get("totalPayments").asLong(0)
                )).one().orElse(new RiskNetwork(customerId, List.of(), List.of(), List.of(), 0));
    }

    private Payment mapPayment(Record record) {
        return new Payment(
                record.get("paymentId").asString(), record.get("customerId").asString(),
                record.get("merchantId").asString(), record.get("instrumentId").asString(),
                new BigDecimal(record.get("amount").toString()), record.get("currency").asString(),
                record.get("channel").asString(), PaymentStatus.valueOf(record.get("status").asString()),
                RiskLevel.valueOf(record.get("riskLevel").asString()), record.get("riskReason").asString(),
                Instant.parse(record.get("createdAt").asString())
        );
    }
}
