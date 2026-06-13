// Consulta 1: Grafo completo de un cliente
MATCH path = (c:Customer {customerId:'CUS-001'})-[*1..3]-(n)
RETURN path;

// Consulta 2: Pagos en revisión o rechazados con sus relaciones principales
MATCH path = (c:Customer)-[:INITIATED]->(p:Payment)-[:PAID_TO|USED|FROM_DEVICE|FROM_IP]->(n)
WHERE p.status IN ['REVIEW','DECLINED']
RETURN path;

// Consulta 3: Clientes que comparten instrumento de pago
MATCH path = (c1:Customer)-[:OWNS]->(i:PaymentInstrument)<-[:OWNS]-(c2:Customer)
WHERE c1.customerId < c2.customerId
RETURN path;

// Consulta 4: Pagos hacia comercios de alto riesgo
MATCH path = (c:Customer)-[:INITIATED]->(p:Payment)-[:PAID_TO]->(m:Merchant {riskLevel:'HIGH'})
RETURN path;

// Consulta 5: Dispositivo riesgoso usado en varios pagos
MATCH path = (p:Payment)-[:FROM_DEVICE]->(d:Device {deviceId:'DEV-RISK-001'})
RETURN path;
