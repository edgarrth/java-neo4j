// Pagos transaccionales de la PoC
UNWIND [
  {id:'PAY-001', c:'CUS-001', i:'CARD-001', m:'MER-001', d:'DEV-001', ip:'190.12.10.1', amount:120.50, currency:'PEN', status:'APPROVED', createdAt:'2026-06-01T10:15:00'},
  {id:'PAY-002', c:'CUS-001', i:'WALLET-001', m:'MER-002', d:'DEV-001', ip:'190.12.10.1', amount:80.00, currency:'PEN', status:'APPROVED', createdAt:'2026-06-01T12:20:00'},
  {id:'PAY-003', c:'CUS-002', i:'CARD-002', m:'MER-003', d:'DEV-002', ip:'190.12.10.2', amount:15.99, currency:'USD', status:'APPROVED', createdAt:'2026-06-02T08:00:00'},
  {id:'PAY-004', c:'CUS-003', i:'CARD-003', m:'MER-004', d:'DEV-003', ip:'181.65.20.7', amount:950.00, currency:'PEN', status:'APPROVED', createdAt:'2026-06-02T11:30:00'},
  {id:'PAY-005', c:'CUS-004', i:'CARD-004', m:'MER-005', d:'DEV-004', ip:'45.33.10.100', amount:1500.00, currency:'USD', status:'REVIEW', createdAt:'2026-06-02T23:10:00'},
  {id:'PAY-006', c:'CUS-005', i:'CARD-005', m:'MER-001', d:'DEV-005', ip:'190.12.10.2', amount:300.00, currency:'PEN', status:'APPROVED', createdAt:'2026-06-03T09:10:00'},
  {id:'PAY-007', c:'CUS-006', i:'CARD-006', m:'MER-006', d:'DEV-RISK-001', ip:'103.44.55.77', amount:700.00, currency:'USD', status:'DECLINED', createdAt:'2026-06-03T03:20:00'},
  {id:'PAY-008', c:'CUS-007', i:'WALLET-002', m:'MER-007', d:'DEV-002', ip:'181.65.20.7', amount:45.00, currency:'PEN', status:'APPROVED', createdAt:'2026-06-04T14:40:00'},
  {id:'PAY-009', c:'CUS-008', i:'CARD-001', m:'MER-008', d:'DEV-RISK-001', ip:'45.33.10.100', amount:260.00, currency:'PEN', status:'REVIEW', createdAt:'2026-06-04T17:05:00'},
  {id:'PAY-010', c:'CUS-009', i:'CARD-005', m:'MER-005', d:'DEV-RISK-001', ip:'45.33.10.100', amount:2100.00, currency:'USD', status:'REVIEW', createdAt:'2026-06-05T01:12:00'},
  {id:'PAY-011', c:'CUS-010', i:'CARD-006', m:'MER-006', d:'DEV-RISK-001', ip:'103.44.55.77', amount:50.00, currency:'USD', status:'DECLINED', createdAt:'2026-06-05T02:30:00'},
  {id:'PAY-012', c:'CUS-001', i:'CARD-001', m:'MER-005', d:'DEV-001', ip:'190.12.10.1', amount:1800.00, currency:'USD', status:'REVIEW', createdAt:'2026-06-05T23:50:00'}
] AS row
MATCH (c:Customer {customerId: row.c})
MATCH (i:PaymentInstrument {instrumentId: row.i})
MATCH (m:Merchant {merchantId: row.m})
MATCH (d:Device {deviceId: row.d})
MATCH (ip:IpAddress {ip: row.ip})
MERGE (p:Payment {paymentId: row.id})
SET p.amount = row.amount,
    p.currency = row.currency,
    p.status = row.status,
    p.createdAt = datetime(row.createdAt)
MERGE (c)-[:INITIATED]->(p)
MERGE (p)-[:USED]->(i)
MERGE (p)-[:PAID_TO]->(m)
MERGE (p)-[:FROM_DEVICE]->(d)
MERGE (p)-[:FROM_IP]->(ip);
