# API Design - PERFORMANCE

## Performance Metrics

| Metric | Good | Needs Improvement | Critical |
|--------|------|-------------------|----------|
| P50 latency | < 50ms | 50-200ms | > 200ms |
| P99 latency | < 200ms | 200-1000ms | > 1000ms |
| Throughput | > 1000 RPS | 100-1000 RPS | < 100 RPS |
| Error rate | < 0.1% | 0.1-1% | > 1% |

## Optimization Techniques

### Response Compression
```yaml
server.compression:
  enabled: true
  mime-types: application/json,text/plain
  min-response-size: 1024
```

### Connection Pooling
```yaml
server.tomcat:
  max-connections: 10000
  threads:
    max: 200
    min-spare: 20
```

### Keep-Alive
```yaml
server.tomcat:
  connection-timeout: 5000
  keep-alive-timeout: 30000
  max-keep-alive-requests: 100
```

### Payload Minimization
| Technique | Before | After |
|-----------|--------|-------|
| Field filtering | 400B | 80B (5x) |
| Compression (gzip) | 80B | 30B (2.7x) |
| Binary (Protobuf) | 400B | 50B (8x) |

## Caching Strategies

### HTTP Caching Headers
```java
@GetMapping("/{id}")
public ResponseEntity<Product> getProduct(@PathVariable String id) {
    Product product = productService.findById(id);
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS)
            .staleWhileRevalidate(300, TimeUnit.SECONDS))
        .eTag(computeETag(product))
        .lastModified(product.getUpdatedAt().toEpochMilli())
        .body(product);
}
```

## Benchmarking APIs

```bash
# Simple load test with wrk
wrk -t12 -c400 -d30s http://localhost:8080/api/v1/products

# Apache Bench
ab -n 10000 -c 100 http://localhost:8080/api/v1/products

# Siege
siege -c 200 -t 60s http://localhost:8080/api/v1/products
```

## Database Query Performance

| API Feature | Query Impact | Optimization |
|------------|-------------|-------------|
| Deep pagination | O(n) scan | Cursor pagination |
| Sorting unindexed field | Full table scan | Add indexes |
| N+1 selects | n+1 queries per page | Eager loading, joins |
| Large response | High network | Field filtering |
