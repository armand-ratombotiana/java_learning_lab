# Performance - Azure Fundamentals

## Key Metrics

| Metric | Target | Warning | Critical |
|--------|--------|---------|----------|
| Response Time (p50) | < 100ms | > 200ms | > 500ms |
| Response Time (p99) | < 500ms | > 1s | > 2s |
| Throughput | > 1000 req/s | < 500 req/s | < 100 req/s |
| Error Rate | < 0.1% | > 1% | > 5% |
| CPU Utilization | < 60% | > 80% | > 90% |
| Memory Utilization | < 70% | > 85% | > 95% |

## Optimization Techniques

### 1. Connection Pooling
`java
HikariConfig config = new HikariConfig();
config.setMaximumPoolSize(20);
config.setMinimumIdle(5);
config.setConnectionTimeout(5000);
config.setIdleTimeout(300000);
config.setMaxLifetime(600000);
`

### 2. Caching Strategy
- **L1 Cache**: In-memory (Caffeine) for hot data
- **L2 Cache**: Distributed (Redis) for shared state
- **Cache-Aside Pattern**: Load on miss, invalidate on write

### 3. Async Processing
- Use CompletableFuture for non-blocking operations
- Implement event-driven architecture with queues
- Batch database operations when possible

### 4. Memory Management
- Pre-size collections when capacity is known
- Use primitive collections for memory efficiency
- Avoid unnecessary object creation in hot paths
- Use StringBuilder for string concatenation

## Benchmarking

### JMH Benchmark Example
`java
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class ServiceBenchmark {
    private Service service;

    @Setup public void setup() { service = new Service(); }

    @Benchmark public void measureProcess() {
        service.process(new Request("test"));
    }
}
`

## Profiling Tools
- Async Profiler
- JFR (Java Flight Recorder)
- JMC (JDK Mission Control)
- Focus on hot methods and allocation sites
