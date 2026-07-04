# Secrets Management Performance

## Vault Performance
- **Read throughput**: 1000-5000 reads/sec per node (depends on storage backend).
- **Write throughput**: 500-2000 writes/sec per node.
- **Latency**: < 5ms read, < 10ms write (local Raft storage).
- **Concurrent connections**: 1000+ concurrent clients per node.

## Scaling Vault
- **HA mode**: Multiple standby nodes forward to active node.
- **Performance standby**: Read-only replicas for high read throughput (Vault Enterprise).
- **Storage backend**: Raft (good), Consul (better performance at scale).
- **Caching**: Vault Agent with caching for high-read workloads.

## Optimization Tips
- **Reduce TLS overhead**: Use mutual TLS with HTTP/2 (default).
- **Token reuse**: Cache tokens instead of requesting new ones for every operation.
- **Batch tokens**: Stateless tokens for high-throughput read-only workloads.
- **Connection pooling**: Reuse connections to Vault (keep-alive).
- **Replication**: Performance replicas for cross-region reads (Enterprise).

## Database Dynamic Secrets Performance
- **Connection pooling**: Vault creates database connections on demand.
- **Credential caching**: Vault caches database credential generation; reduces DB load.
- **Lease management**: Short TTLs increase load on Vault and database.
- **User cleanup**: Vault must clean up expired users; monitor for zombie users.

## Monitoring Vault
- **Metrics**: Prometheus endpoint (`/v1/sys/metrics`).
- **Key metrics**: Request rate, latency, storage backend performance, seal status.
- **Dashboard**: Grafana dashboard for Vault telemetry.
- **Alerts**: Vault sealed, storage backend pressure, high error rates.
