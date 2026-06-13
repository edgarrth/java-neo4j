// Clientes
UNWIND [
  {id:'CUS-001', name:'Ana Torres', document:'77889911', segment:'PREMIUM', country:'PE'},
  {id:'CUS-002', name:'Luis Vargas', document:'44556677', segment:'MASS', country:'PE'},
  {id:'CUS-003', name:'Maria Flores', document:'11223344', segment:'MASS', country:'PE'},
  {id:'CUS-004', name:'Carlos Rios', document:'99887766', segment:'AFFLUENT', country:'PE'},
  {id:'CUS-005', name:'Sofia Peña', document:'66554433', segment:'PREMIUM', country:'PE'},
  {id:'CUS-006', name:'Diego Salas', document:'33445566', segment:'MASS', country:'PE'},
  {id:'CUS-007', name:'Valeria Soto', document:'22334455', segment:'AFFLUENT', country:'PE'},
  {id:'CUS-008', name:'Jorge Leon', document:'88776655', segment:'MASS', country:'PE'},
  {id:'CUS-009', name:'Lucia Campos', document:'55667788', segment:'PREMIUM', country:'PE'},
  {id:'CUS-010', name:'Ricardo Molina', document:'12345678', segment:'MASS', country:'PE'}
] AS row
MERGE (c:Customer {customerId: row.id})
SET c.fullName = row.name, c.documentNumber = row.document, c.segment = row.segment, c.country = row.country;

// Comercios
UNWIND [
  {id:'MER-001', name:'Tech Store SAC', mcc:'5732', category:'Electronics', country:'PE', risk:'LOW'},
  {id:'MER-002', name:'Market Express', mcc:'5411', category:'Groceries', country:'PE', risk:'LOW'},
  {id:'MER-003', name:'Streaming Plus', mcc:'4899', category:'Digital Services', country:'US', risk:'LOW'},
  {id:'MER-004', name:'Travel Now', mcc:'4722', category:'Travel', country:'PE', risk:'MEDIUM'},
  {id:'MER-005', name:'Crypto Fast Exchange', mcc:'6051', category:'Financial Services', country:'PA', risk:'HIGH'},
  {id:'MER-006', name:'Game Coins Online', mcc:'7995', category:'Gaming', country:'MT', risk:'HIGH'},
  {id:'MER-007', name:'Pharma Salud', mcc:'5912', category:'Pharmacy', country:'PE', risk:'LOW'},
  {id:'MER-008', name:'Fashion Outlet', mcc:'5651', category:'Retail', country:'PE', risk:'LOW'}
] AS row
MERGE (m:Merchant {merchantId: row.id})
SET m.legalName = row.name, m.mcc = row.mcc, m.category = row.category, m.country = row.country, m.riskLevel = row.risk;

// Instrumentos de pago
UNWIND [
  {id:'CARD-001', type:'CARD', brand:'VISA', last4:'4242', fingerprint:'fp-card-001'},
  {id:'CARD-002', type:'CARD', brand:'MASTERCARD', last4:'1111', fingerprint:'fp-card-002'},
  {id:'CARD-003', type:'CARD', brand:'VISA', last4:'2222', fingerprint:'fp-card-003'},
  {id:'CARD-004', type:'CARD', brand:'AMEX', last4:'3333', fingerprint:'fp-card-004'},
  {id:'CARD-005', type:'CARD', brand:'VISA', last4:'4444', fingerprint:'fp-card-005'},
  {id:'CARD-006', type:'CARD', brand:'MASTERCARD', last4:'5555', fingerprint:'fp-card-006'},
  {id:'WALLET-001', type:'WALLET', brand:'INTERNAL', last4:'0001', fingerprint:'fp-wallet-001'},
  {id:'WALLET-002', type:'WALLET', brand:'INTERNAL', last4:'0002', fingerprint:'fp-wallet-002'}
] AS row
MERGE (i:PaymentInstrument {instrumentId: row.id})
SET i.type = row.type, i.brand = row.brand, i.last4 = row.last4, i.fingerprint = row.fingerprint;

// Relación cliente -> instrumento
UNWIND [
  ['CUS-001','CARD-001'], ['CUS-001','WALLET-001'], ['CUS-002','CARD-002'], ['CUS-003','CARD-003'],
  ['CUS-004','CARD-004'], ['CUS-005','CARD-005'], ['CUS-006','CARD-006'], ['CUS-007','WALLET-002'],
  ['CUS-008','CARD-001'], ['CUS-009','CARD-005'], ['CUS-010','CARD-006']
] AS rel
MATCH (c:Customer {customerId: rel[0]})
MATCH (i:PaymentInstrument {instrumentId: rel[1]})
MERGE (c)-[:OWNS]->(i);

// Dispositivos e IPs
UNWIND [
  {id:'DEV-001', type:'ANDROID'}, {id:'DEV-002', type:'IOS'}, {id:'DEV-003', type:'WEB'},
  {id:'DEV-004', type:'ANDROID'}, {id:'DEV-005', type:'IOS'}, {id:'DEV-RISK-001', type:'EMULATOR'}
] AS row
MERGE (d:Device {deviceId: row.id}) SET d.type = row.type;

UNWIND ['190.12.10.1','190.12.10.2','181.65.20.7','45.33.10.100','103.44.55.77'] AS ip
MERGE (:IpAddress {ip: ip});
