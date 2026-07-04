# Performance: Connection Pool Tuning

## Optimal Pool Size
- Too small: requests queue up, response time increases
- Too large: database overwhelmed, context switching overhead
- Rule of thumb: `maxPoolSize = CPUS * 2 + effective_disks`

## Pool Comparison

| Pool | Performance | Features | Default in |
|---|---|---|---|
| HikariCP | Fastest | Minimal, robust | Spring Boot 2+ |
| Tomcat DBCP 2 | Good | JNDI integration | Spring Boot 1.x |
| Commons DBCP 2 | Moderate | Legacy | Legacy apps |
| c3p0 | Slow | Auto-sizing | Legacy |
| ViburDBCP | Good | Pluggable | Projects needing custom pools |

## Performance Micro-Optimizations
HikariCP achieves speed through:
- `FastList` – removes range checking from ArrayList
- `ConcurrentBag` – lock-free for common paths (ThreadLocal cache)
- `ProxyConnection` – minimal overhead proxy for close() interception
- `copyOnWrite` config – allows live config changes without synchronization

## Monitoring Performance
```properties
spring.datasource.hikari.register-mbeans=true
```
Key metrics:
- `connections.timeout`: connection request timeouts (should be 0)
- `connections.acquire`: average time to acquire a connection
- `connections.usage`: average time connections are held
