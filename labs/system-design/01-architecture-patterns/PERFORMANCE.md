# Architecture Patterns - PERFORMANCE

## Layered Architecture

### Performance Characteristics
- **Latency per request**: 1-5ms (in-process) / 10-50ms (with DB)
- **Throughput**: Limited by vertical scaling
- **Bottleneck**: Typically the database connection pool

### Optimization Tips
- Add caching at service layer (Caffeine, Redis)
- Use lazy loading for ORM associations
- Batch database operations
- Connection pooling (HikariCP)

## Microservices

### Performance Characteristics
- **Latency per request**: 50-500ms (network hops)
- **Throughput**: Scales horizontally (add more instances)
- **Bottleneck**: Inter-service communication

### Optimization Tips
- Use gRPC instead of REST for internal calls (binary protocol, HTTP/2)
- Implement caching at API Gateway level
- Use connection pooling for HTTP clients
- Async communication via message brokers for non-critical paths

## Event-Driven

### Performance Characteristics
- **Latency per event**: 1-100ms (broker dependent)
- **Throughput**: 100K-1M+ events/sec (Kafka)
- **Bottleneck**: Consumer processing speed

### Optimization Tips
- Tune Kafka: `batch.size`, `linger.ms`, `compression.type`
- Parallelize consumers within a consumer group
- Use partitioning with appropriate keys
- Batch database writes in consumers

## CQRS

### Performance Characteristics
- **Write latency**: 5-50ms (event store)
- **Read latency**: 1-10ms (denormalized read model)
- **Bottleneck**: Projection update speed

### Optimization Tips
- Denormalize read models for specific query patterns
- Use materialized views in PostgreSQL or Elasticsearch
- Batch projection updates
- Cache read models aggressively

## Comparison

| Metric | Layered | Microservices | Event-Driven | CQRS |
|--------|---------|---------------|--------------|------|
| P50 Latency | 10ms | 100ms | 50ms | 5ms (read) |
| Throughput | 1K req/s | 10K req/s | 100K evt/s | 50K req/s |
| Scale Unit | Vertical | Horizontal | Horizontal | Horizontal |
| Complexity | Low | High | Medium | High |
