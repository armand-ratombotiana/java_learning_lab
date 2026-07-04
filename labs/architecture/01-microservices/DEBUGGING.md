# Debugging Microservices

## Distributed Tracing Setup
```yaml
# application.yml - all services
spring:
  sleuth:
    sampler:
      probability: 1.0  # 100% sampling for debugging
  zipkin:
    base-url: http://localhost:9411
```

## Common Debugging Scenarios

### 1. Request Tracing
```java
// Trace ID propagation
@Bean
public FilterRegistrationBean<TraceFilter> tracingFilter() {
    // Automatically adds traceId/spanId to MDC
    return new FilterRegistrationBean<>(new TraceFilter());
}
```

### 2. Circuit Breaker State Debugging
```bash
# Actuator endpoint to check circuit breaker state
curl http://localhost:8081/actuator/circuitbreakers
```

### 3. Log Aggregation with Correlation IDs
```java
@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        // Propagate trace ID in headers
        request.getHeaders().add("X-Trace-Id",
            MDC.get("traceId"));
        return execution.execute(request, body);
    }
}
```

### 4. Debugging Network Issues
```bash
# Check service connectivity
docker-compose exec order-service curl http://payment-service:8082/actuator/health

# Check DNS resolution
docker-compose exec order-service nslookup payment-service
```
