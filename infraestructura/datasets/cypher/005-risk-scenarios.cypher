// Escenarios de riesgo para demostrar consultas de grafos.
// 1) Un mismo instrumento usado por más de un cliente.
MATCH (c1:Customer {customerId:'CUS-001'}), (c2:Customer {customerId:'CUS-008'}), (i:PaymentInstrument {instrumentId:'CARD-001'})
MERGE (c1)-[:SHARES_INSTRUMENT_WITH {reason:'same card fingerprint'}]->(c2);

MATCH (c1:Customer {customerId:'CUS-005'}), (c2:Customer {customerId:'CUS-009'}), (i:PaymentInstrument {instrumentId:'CARD-005'})
MERGE (c1)-[:SHARES_INSTRUMENT_WITH {reason:'same card fingerprint'}]->(c2);

// 2) Dispositivo sospechoso conectado a varios pagos rechazados o en revisión.
MATCH (d:Device {deviceId:'DEV-RISK-001'})
SET d.riskLevel = 'HIGH', d.reason = 'multiple reviewed/declined payments';

// 3) Comercios de alto riesgo.
MATCH (m:Merchant)
WHERE m.riskLevel = 'HIGH'
SET m.requiresEnhancedMonitoring = true;

// 4) Marcadores de risk score por pago.
MATCH (p:Payment)-[:PAID_TO]->(m:Merchant)
SET p.riskScore = CASE
  WHEN p.status = 'DECLINED' THEN 95
  WHEN p.status = 'REVIEW' AND m.riskLevel = 'HIGH' THEN 85
  WHEN p.amount >= 1000 THEN 70
  ELSE 20
END;
