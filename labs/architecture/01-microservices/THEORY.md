# Microservices Theory

## Core Principles
1. **Single Responsibility** - Each service handles one business capability
2. **Autonomous Deployment** - Services deploy independently
3. **Decentralized Data** - Each service owns its database
4. **Failure Isolation** - Failure in one service doesn't cascade
5. **Technology Agnostic** - Services can use different tech stacks

## Service Decomposition Strategies
### By Business Capability
```java
@Service
public class OrderService {
    public Order createOrder(OrderRequest request) { ... }
    public Order getOrder(Long id) { ... }
}
```

### By Subdomain (DDD)
Identify bounded contexts and map each to a service.

### By Use Case
Group related use cases into services.

## Communication Patterns
### Synchronous (REST/gRPC)
```java
@FeignClient(name = "payment-service", url = "${payment.service.url}")
public interface PaymentClient {
    @PostMapping("/api/payments")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
}
```

### Asynchronous (Messaging)
```java
@Service
public class OrderEventPublisher {
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderCreated(OrderEvent event) {
        kafkaTemplate.send("order-events", event);
    }
}
```

## API Gateway Pattern
```java
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order-service", r -> r.path("/api/orders/**")
                .uri("lb://ORDER-SERVICE"))
            .route("payment-service", r -> r.path("/api/payments/**")
                .uri("lb://PAYMENT-SERVICE"))
            .build();
    }
}
```

## Service Discovery
```java
@Configuration
@EnableDiscoveryClient
public class ServiceConfig {
    // Automatically registers with Eureka/Consul
}
```
