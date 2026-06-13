// Limpia la base de datos para cargar la PoC desde cero.
// Usar solo en entorno local/demo.
MATCH (n) DETACH DELETE n;
