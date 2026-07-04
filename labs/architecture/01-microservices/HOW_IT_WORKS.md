# How Microservices Works

## Architecture Flow
1. **Client request** reaches API Gateway
2. **Gateway** routes to appropriate service
3. **Service discovery** resolves service location
4. **Service** processes request, may call other services
5. **Distributed tracing** tracks request across services
6. **Response** flows back through the chain

## Service Communication Flow
```java
// Request flow with distributed tracing
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest req) {
        // 1. Validate request
        // 2. Call inventory service via Feign
        // 3. Call payment service via messaging
        // 4. Persist order
        // 5. Return response with trace ID
    }
}
```

## Infrastructure Components
- **API Gateway** - Single entry point, routing, auth, rate limiting
- **Service Registry** - Eureka/Consul for service discovery
- **Config Server** - Externalized configuration management
- **Message Broker** - Kafka/RabbitMQ for async communication
- **Distributed Tracing** - Zipkin/Jaeger for observability

## Deployment Pattern
```yaml
# Each service has its own CI/CD pipeline
build:
  - compile
  - test
  - package (Docker image)
deploy:
  - blue/green deployment
  - health check verification
  - traffic switch
```
