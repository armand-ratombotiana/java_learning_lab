# HTTP Protocol - Why It Matters

## Impact on Modern Computing

HTTP is the most widely used application-layer protocol on the internet.

### 1. Universal Interface

HTTP serves as the universal API protocol for web services, mobile backends, and microservices.

```java
@RestController
public class OrderController {
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }
}
```

### 2. Performance Impact

| Aspect | HTTP/1.1 | HTTP/2 | HTTP/3 |
|--------|----------|--------|--------|
| Connections per page | 6-30+ (domain sharding) | 1 | 1 |
| First byte latency | 3-4 RTT | 2 RTT | 0-1 RTT |
| Header overhead | ~800 bytes | ~50 bytes (compressed) | Compressed |
| Throughput (lossy network) | Degraded | Degraded | 30%+ better |

### 3. Caching and Scalability

HTTP caching handles significant traffic:
- Browser cache reduces repeat requests by 40-60%
- CDNs serve 70-90% of content from edge
- Proper Cache-Control headers reduce server load

```java
@GetMapping("/products/{id}")
public ResponseEntity<Product> getProduct(@PathVariable Long id) {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
        .eTag(Integer.toHexString(product.hashCode()))
        .body(productService.findById(id));
}
```

### 4. Security Foundation

TLS over HTTP (HTTPS) secures modern web traffic.

```java
public class TracingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        String traceId = UUID.randomUUID().toString();
        ((HttpServletResponse) res).setHeader("X-Trace-Id", traceId);
        chain.doFilter(req, res);
    }
}
```
