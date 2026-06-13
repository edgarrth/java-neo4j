# Neo4j Payment Processing PoC

PoC en **Java** con **Spring Boot**, **arquitectura hexagonal**, **DDD** y **Neo4j** para modelar un caso de **payment processing** basado en grafos.

El caso de uso sí aplica a Neo4j porque en pagos es útil analizar relaciones entre clientes, comercios, instrumentos de pago y transacciones. Neo4j permite detectar patrones como tarjetas compartidas por varios clientes, pagos rechazados recurrentes, comercios relacionados a pagos riesgosos y redes transaccionales sospechosas.

## Arquitectura

```text
src/main/java/com/edgarrt/poc/paymentprocessing
├── domain
│   ├── model              # Entidades y value objects del dominio
│   └── service            # Política de riesgo del dominio
├── application
│   ├── port/in            # Casos de uso
│   ├── port/out           # Puertos de persistencia
│   └── service            # Orquestación de casos de uso
└── infrastructure
    ├── adapter/in/rest    # API REST
    ├── out/neo4j          # Adaptador Neo4j con Cypher
    └── config             # Configuración de beans
```

## Modelo de grafo

```text
(Customer)-[:INITIATED]->(Payment)-[:PAID_TO]->(Merchant)
(Payment)-[:USED_INSTRUMENT]->(PaymentInstrument)
(Customer)-[:OWNS_OR_USES]->(PaymentInstrument)
```

## Requisitos

- JDK 25
- Maven 3.9+
- Docker y Docker Compose
- IntelliJ IDEA

## Levantar Neo4j

```bash
cd infraestructura
docker compose up -d
```

Consola Neo4j:

- URL: http://localhost:7474
- User: `neo4j`
- Password: `paymentspoc123`

Ejecutar constraints y datos iniciales:

```bash
docker exec -i neo4j-payment-processing cypher-shell -u neo4j -p paymentspoc123 < neo4j/init.cypher
```

## Levantar el microservicio

Desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

## Configuración

La aplicación usa `src/main/resources/properties.yml`, importado desde `application.yml`.

```yaml
spring:
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: paymentspoc123
```

## Endpoints REST

### Crear cliente

```http
POST /payments/v1/customers
Content-Type: application/json

{
  "customerId": "CUS-100",
  "fullName": "Edgar Rodriguez",
  "documentNumber": "12345678",
  "segment": "PREMIUM"
}
```

### Crear comercio

```http
POST /payments/v1/merchants
Content-Type: application/json

{
  "merchantId": "MER-100",
  "legalName": "Payment Processing Store SAC",
  "mcc": "5732",
  "country": "PE"
}
```

### Registrar instrumento de pago

```http
POST /payments/v1/instruments
Content-Type: application/json

{
  "instrumentId": "CARD-100",
  "type": "CARD",
  "fingerprint": "fp-card-100",
  "brand": "VISA",
  "last4": "4242"
}
```

### Autorizar pago

```http
POST /payments/v1/payments/authorize
Content-Type: application/json

{
  "paymentId": "PAY-100",
  "customerId": "CUS-100",
  "merchantId": "MER-100",
  "instrumentId": "CARD-100",
  "amount": 250.50,
  "currency": "PEN",
  "channel": "MOBILE_APP"
}
```

Response esperado:

```json
{
  "code": "OK",
  "message": "Operation completed",
  "data": {
    "paymentId": "PAY-100",
    "customerId": "CUS-100",
    "merchantId": "MER-100",
    "instrumentId": "CARD-100",
    "amount": 250.50,
    "currency": "PEN",
    "channel": "MOBILE_APP",
    "status": "AUTHORIZED",
    "riskLevel": "LOW",
    "riskReason": "No relevant graph risk signals found"
  }
}
```

### Consultar pago

```http
GET /payments/v1/payments/PAY-100
```

### Analizar riesgo de pago

```http
GET /payments/v1/payments/PAY-100/risk-analysis
```

### Ver red de riesgo de cliente

```http
GET /payments/v1/customers/CUS-100/risk-network
```

## Archivo de pruebas HTTP

Se incluye:

```text
infraestructura/http/payment-processing.http
```

Puedes abrirlo en IntelliJ IDEA y ejecutar cada request directamente.

## Consultas Cypher útiles

```cypher
MATCH (c:Customer)-[:INITIATED]->(p:Payment)-[:PAID_TO]->(m:Merchant)
MATCH (p)-[:USED_INSTRUMENT]->(i:PaymentInstrument)
RETURN c,p,m,i;
```

```cypher
MATCH (c1:Customer)-[:OWNS_OR_USES]->(i:PaymentInstrument)<-[:OWNS_OR_USES]-(c2:Customer)
WHERE c1.customerId <> c2.customerId
RETURN c1.customerId, i.instrumentId, c2.customerId;
```

## Importar en IntelliJ IDEA

1. Descomprime el ZIP.
2. Abre IntelliJ IDEA.
3. Selecciona `Open`.
4. Elige la carpeta `neo4j-payment-processing-poc`.
5. IntelliJ detectará el proyecto Maven.
6. Configura SDK Java 25.
7. Levanta Neo4j con Docker Compose.
8. Ejecuta `PaymentProcessingApplication`.
