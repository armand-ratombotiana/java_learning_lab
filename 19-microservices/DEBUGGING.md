# Debugging Microservices Communication Issues

## Common Failure Scenarios

### Service Discovery Failures

Service discovery failures occur when services cannot locate each other. This manifests as connection refused errors, services appearing unavailable, or intermittent 502/503 gateway errors. The root cause is typically service registry issues, network configuration problems, or startup timing.

When using Eureka or Consul, services must register themselves before being discoverable. A common issue is the registration delay exceeding client timeouts. If a service starts and immediately tries to call another service, the target may not be registered yet. Configure appropriate retry logic and initial delay in the discovery client to handle this.

Network segmentation prevents services from communicating across namespaces or VPCs. Services in different Kubernetes namespaces cannot resolve each other by default. Ensure proper DNS resolution, service mesh configuration, or ingress rules allow traffic between required services.

### Inter-Service Communication Timeouts

Timeout errors between services indicate the called service is slow or unresponsive. This can cascade into complete system failure if not handled properly. The calling service receives timeout exceptions, and the response latency spikes.

The primary causes are slow downstream services, excessive processing load, or resource exhaustion. Check database connections, external API responses, and CPU/memory pressure on the target service. Implementing circuit breakers prevents cascading failures by failing fast when a service is unhealthy.

Thread pool exhaustion causes requests to queue up and eventually timeout. Review the thread pool configuration for your HTTP client and verify connection pool settings match the expected load. Default settings are often insufficient for production workloads.

### Stack Trace Examples

**Feign client fallback:**
```
feign.RetryableException: Connection refused executing POST http://service-name/api/endpoint
    at feign.Client$Default.convertAndExecute(Client.java:1234)
```

**Ribbon load balancer timeout:**
```
com.netflix.hystrix.HystrixTimeoutException: GatewayTimeoutException
    at com.netflix.hystrix.AbstractCommand$22.call(AbstractCommand.java:567)
```

**Eureka registration failure:**
```
com.netflix.discovery.DiscoveryClient: DiscoveryClient_SERVICE_ID - was unable to send heartbeat!
```

## Debugging Techniques

### Tracing Service Calls

Distributed tracing shows the complete path of a request across services. Zipkin or Jaeger provides visual representations of request flow, helping identify which service introduces latency. Each span includes timing information for HTTP calls, database operations, and custom logic.

Add correlation IDs to logs and propagate them across service boundaries. Include a unique request ID in all log statements and HTTP headers. This enables searching across multiple service logs to reconstruct the full request path.

Enable debug logging for Feign/HTTP clients to see request/response details. Look for connection pool exhaustion, SSL handshake failures, or malformed responses.

### Health Check Verification

Verify all health endpoints return healthy status. Spring Boot Actuator provides `/health` with details about dependencies. Check database connectivity, external service availability, and disk space from the health endpoint.

For Kubernetes, use `kubectl describe pod` to see pod events and status. Check for OOM kills, image pull failures, or liveness probe failures. Use `kubectl logs` with `--previous` to see logs from crashed containers.

Use `kubectl exec` to enter containers and test connectivity directly. Use `curl` or `nc` to verify network connectivity between pods. Check DNS resolution with `nslookup` or `dig`.

## Best Practices

Implement circuit breakers with Resilience4j or Hystrix. Configure fallback methods for graceful degradation when downstream services fail. Set appropriate timeout and retry values based on expected latency.

Use bulkheads to isolate failures and prevent system-wide impact. Configure thread pool sizes and semaphore limits to bound resource usage under load.

Log structured JSON with correlation IDs for easy log aggregation. Set up alerts on error rates, latency percentiles, and circuit breaker state changes.