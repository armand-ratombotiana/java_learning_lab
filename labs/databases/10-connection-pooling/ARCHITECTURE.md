# Architecture: Connection Pooling in Multi-Tenant Systems

## Multi-Tenant Pool Architectures

### Single Pool per Tenant
```
App → Tenant A → Pool A → DB A
   → Tenant B → Pool B → DB B
```
**Pros**: Isolation, per-tenant tuning
**Cons**: More connections total, resource overhead

### Shared Pool with Tenant Routing
```
App → Shared Pool → TenantAwareDataSource → DB A / DB B
```
**Pros**: Resource efficiency
**Cons**: Complex routing logic

### Separate Pools for Workload Types
```
App → OLTP Pool (10)     → OLTP DB
   → Reporting Pool (30) → Reporting DB replica
   → Batch Pool (50)     → Batch DB replica
```

## Microservice Considerations
- Each service instance gets its own pool
- Pool size depends on instance count:
  ```
  per-instance-pool = total-pool / instance-count
  ```
- Kubernetes: use Ready/Probe checks with pool health

## Disaster Recovery
- Pools should fail over to replica on primary outage
- HikariCP supports `spring.datasource.hikari.connection-init-sql` for failover setup
- Use read replicas with separate pools for read/write splitting
