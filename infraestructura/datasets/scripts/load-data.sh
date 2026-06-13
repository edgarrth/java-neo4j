#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME="${CONTAINER_NAME:-neo4j-payment-processing}"
USER="${NEO4J_USER:-neo4j}"
PASSWORD="${NEO4J_PASSWORD:-paymentspoc123}"
IMPORT_DIR="/var/lib/neo4j/import/sample-data/cypher"

for file in \
  001-clean-db.cypher \
  002-constraints-and-indexes.cypher \
  003-master-data.cypher \
  004-payments.cypher \
  005-risk-scenarios.cypher; do
  echo "Running $file"
  docker exec -i "$CONTAINER_NAME" cypher-shell -u "$USER" -p "$PASSWORD" -f "$IMPORT_DIR/$file"
done

echo "Data loaded successfully. Open Neo4j Browser: http://localhost:7474"
