# Performance — Spring Boot Internals

## Startup Optimization

### Lazy Initialization

```properties
spring.main.lazy-initialization=true
```

Defers bean creation until first use. Reduces startup time but increases per-operation latency.

### Auto-Configuration Exclusions

```java
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    SecurityAutoConfiguration.class
})
```

Exclude unnecessary auto-configuration to reduce class scanning and conditional evaluation.

### Embedded Server Comparison

| Metric | Tomcat | Jetty | Undertow |
|--------|--------|-------|----------|
| Startup (ms) | ~1800 | ~1400 | ~1200 |
| Memory (MB) | ~120 | ~95 | ~85 |
| Throughput (req/s) | ~8500 | ~8200 | ~9100 |
| First req latency (ms) | ~45 | ~40 | ~35 |

### Spring AOT Processing

Spring Boot 3.x AOT (Ahead-of-Time) compilation:
- Analyzes `@Conditional` at compile time
- Generates optimized initialization code
- Reduces startup to ~300ms with GraalVM native

## Metrics Backend Performance

| Backend | Write Overhead | Read Latency | Storage |
|---------|---------------|--------------|---------|
| SimpleMeterRegistry | ~50ns | ~100ns | In-memory |
| Micrometer + Prometheus | ~200ns | ~1µs | Scrape |
| Micrometer + Datadog | ~5µs | ~10µs | Agent |
| Micrometer + AWS CloudWatch | ~20ms | ~50ms | API |

## Health Indicator Performance

Health endpoint response time scales linearly with number of indicators:
- 10 health indicators: ~50ms total
- 100 health indicators: ~500ms total
- Set `management.endpoint.health.show-details=when-authorized` to avoid serialization cost

## MessageConverter Performance

| Converter | Throughput | Object Size | Time |
|-----------|-----------|------------|------|
| `MappingJackson2HttpMessageConverter | 12K req/s | 1KB | 80µs |
| `StringHttpMessageConverter` | 65K req/s | 1KB | 15µs |
| `ByteArrayHttpMessageConverter` | 200K req/s | 1KB | 5µs |
| ProtobufHttpMessageConverter | 18K req/s | 1KB | 55µs |

## BeanFactoryPostProcessor Performance

- Each `BeanFactoryPostProcessor` adds ~5-20ms to startup
- Each `BeanPostProcessor` adds ~2-10µs per bean creation
- Minimize custom processors for fast startup in serverless environments