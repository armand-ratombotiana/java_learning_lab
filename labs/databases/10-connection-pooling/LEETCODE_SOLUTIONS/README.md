# LEETCODE_SOLUTIONS — Connection Pooling

## Connection Management Solutions

| LeetCode Topic | Connection Pooling Concept | Best Practice |
|---------------|---------------------------|---------------|
| High QPS queries | Pool sizing formula | Size = Tn * (Cm - 1) + 1 |
| Slow queries | Connection timeout config | `connectionTimeout: 30000` |
| N+1 problem | Statement caching | PreparedStatement cache |
| Concurrent requests | HikariCP maximum-pool-size | Start with 10-20 connections |

### HikariCP Configuration for LeetCode Scenarios
```properties
# Small API service (LeetCode-level traffic)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# Pool sizing formula
# Pool Size = Tn * (Cm - 1) + 1
# Tn = number of threads, Cm = max connections per thread
```
