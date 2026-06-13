$ContainerName = if ($env:CONTAINER_NAME) { $env:CONTAINER_NAME } else { "neo4j-payment-processing" }
$Neo4jUser = if ($env:NEO4J_USER) { $env:NEO4J_USER } else { "neo4j" }
$Neo4jPassword = if ($env:NEO4J_PASSWORD) { $env:NEO4J_PASSWORD } else { "paymentspoc123" }
$ImportDir = "/var/lib/neo4j/import/sample-data/cypher"

$Files = @(
  "001-clean-db.cypher",
  "002-constraints-and-indexes.cypher",
  "003-master-data.cypher",
  "004-payments.cypher",
  "005-risk-scenarios.cypher"
)

foreach ($File in $Files) {
  Write-Host "Running $File"
  docker exec -i $ContainerName cypher-shell -u $Neo4jUser -p $Neo4jPassword -f "$ImportDir/$File"
}

Write-Host "Data loaded successfully. Open Neo4j Browser: http://localhost:7474"
