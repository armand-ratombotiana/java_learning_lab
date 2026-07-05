# Performance: Banking Platform

## Throughput Targets
- Payment processing: < 500ms P99 latency
- Balance inquiry: < 50ms P99
- Fraud evaluation: < 200ms (rule engine) + async ML
- Notification delivery: < 5s end-to-end

## Bottlenecks
- **Database contention**: Frequent balance updates create lock contention on account rows. Mitigate with optimistic locking + retry.
- **Serialization overhead**: JSON serialization on every inter-service call. Consider Protobuf for internal RPC.
- **Event processing lag**: Kafka consumer lag spikes during traffic bursts. Tune partition count and consumer group size.

## Optimization Strategies
- Use Redis for balance cache (with 1s TTL, write-through)
- Pre-compute fraud feature vectors asynchronously
- Batch notification deliveries
- Connection pooling with HikariCP (size = 2x CPU cores)
- Read replicas for ledger queries
