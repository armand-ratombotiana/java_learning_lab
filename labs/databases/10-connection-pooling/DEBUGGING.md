# Debugging: Connection Pooling

## Common Issues

| Symptom | Cause | Fix |
|---|---|---|
| `Connection is not available, request timed out` | Pool exhausted | Increase `maxPoolSize` or optimize query time |
| Connections stuck in active | Connection leak | Enable leak detection, check try-with-resources |
| Slow first query | Connection not validated | Set `connection-test-query` |
| Pool initialization failure | Wrong DB URL/credentials | Verify `spring.datasource.url`, username, password |
| Connections closed by database | `maxLifetime` > DB timeout | Set `max-lifetime` < DB `wait_timeout` |

## Enable Leak Detection
```properties
spring.datasource.hikari.leak-detection-threshold=30000
```
This logs a stack trace when a connection is held longer than 30s.

## Check Pool Metrics
```bash
# Actuator
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
curl http://localhost:8080/actuator/metrics/hikaricp.connections.timeout

# JMX
jconsole → MBeans → com.zaxxer.hikari
```

## Debug Logging
```properties
logging.level.com.zaxxer.hikari=DEBUG
logging.level.com.zaxxer.hikari.HikariConfig=TRACE
logging.level.com.zaxxer.hikari.pool.HikariPool=TRACE
```
