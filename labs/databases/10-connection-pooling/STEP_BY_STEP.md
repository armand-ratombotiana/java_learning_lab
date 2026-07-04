# Step by Step: HikariCP Configuration Tuning

## Step 1: Start with Defaults
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=10
```
Spring Boot auto-configures these by default.

## Step 2: Enable Metrics
```properties
spring.datasource.hikari.register-mbeans=true
management.endpoints.web.exposure.include=metrics
```

## Step 3: Monitor at Peak Load
Check during load testing:
- `hikaricp.connections.active` – connections currently in use
- `hikaricp.connections.idle` – available connections
- `hikaricp.connections.pending` – threads waiting for a connection
- `hikaricp.connections.timeout` – connection request timeouts

## Step 4: Tune Pool Size
If `pending > 0` and `active == maximum-pool-size`: increase pool size.
If `active < maximum-pool-size * 0.5` at peak: decrease pool size.

## Step 5: Set Timeouts
```properties
# Connection timeout: how long to wait for a connection
spring.datasource.hikari.connection-timeout=5000
# Max lifetime: rotate connections before DB drops them
spring.datasource.hikari.max-lifetime=1800000
# Idle timeout: remove connections not used for 10 min
spring.datasource.hikari.idle-timeout=600000
```

## Step 6: Enable Leak Detection (Dev Only)
```properties
spring.datasource.hikari.leak-detection-threshold=30000
# Logs stack trace if connection held > 30s
```

## Step 7: Validate Configuration
```bash
# Check pool state via JMX
jconsole -local
# MBeans → com.zaxxer.hikari → HikariPoolMXBean
```
