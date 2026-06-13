CREATE CONSTRAINT customer_id IF NOT EXISTS FOR (c:Customer) REQUIRE c.customerId IS UNIQUE;
CREATE CONSTRAINT merchant_id IF NOT EXISTS FOR (m:Merchant) REQUIRE m.merchantId IS UNIQUE;
CREATE CONSTRAINT instrument_id IF NOT EXISTS FOR (i:PaymentInstrument) REQUIRE i.instrumentId IS UNIQUE;
CREATE CONSTRAINT payment_id IF NOT EXISTS FOR (p:Payment) REQUIRE p.paymentId IS UNIQUE;
CREATE INDEX payment_status IF NOT EXISTS FOR (p:Payment) ON (p.status);
CREATE INDEX payment_created_at IF NOT EXISTS FOR (p:Payment) ON (p.createdAt);

MERGE (c1:Customer {customerId:'CUS-001'}) SET c1.fullName='Ana Torres', c1.documentNumber='77889911', c1.segment='PREMIUM'
MERGE (c2:Customer {customerId:'CUS-002'}) SET c2.fullName='Luis Vargas', c2.documentNumber='44556677', c2.segment='MASS'
MERGE (m1:Merchant {merchantId:'MER-001'}) SET m1.legalName='Tech Store SAC', m1.mcc='5732', m1.country='PE'
MERGE (i1:PaymentInstrument {instrumentId:'CARD-001'}) SET i1.type='CARD', i1.fingerprint='fp-card-001', i1.brand='VISA', i1.last4='4242'
MERGE (i2:PaymentInstrument {instrumentId:'CARD-002'}) SET i2.type='CARD', i2.fingerprint='fp-card-002', i2.brand='MASTERCARD', i2.last4='1111';
