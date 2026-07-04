# API Gateways - Theory

## Gateway Patterns

### 1. Reverse Proxy
Acts as an intermediary accepting requests from clients and forwarding them to backend services.

### 2. API Composition
Aggregates responses from multiple microservices into a single response.

### 3. Cross-Cutting Concerns
Centralizes authentication, logging, rate limiting, and monitoring.

## Spring Cloud Gateway Features
- **Routing**: Route requests to appropriate services
- **Predicates**: Match requests based on path, headers, query params, etc.
- **Filters**: Modify requests/responses (add headers, rate limit, circuit break)
- **Load Balancing**: Integrates with Eureka/Consul/Kubernetes
- **Security**: OAuth2, JWT validation at gateway level

## Route Configuration
```java
@Bean
public RouteLocator customRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("user-service", r -> r
            .path("/api/users/**")
            .filters(f -> f
                .stripPrefix(1)
                .circuitBreaker(config -> config
                    .setName("userServiceCB")
                    .setFallbackUri("forward:/fallback/users"))
                .retry(3)
                .requestRateLimiter(config -> config
                    .setRateLimiter(redisRateLimiter())))
            .uri("lb://user-service"))
        .route("order-service", r -> r
            .path("/api/orders/**")
            .filters(f -> f
                .addRequestHeader("X-Gateway", "true")
                .addResponseHeader("X-Response-Time",
                    LocalDateTime.now().toString()))
            .uri("lb://order-service"))
        .build();
}

@Bean
public RedisRateLimiter redisRateLimiter() {
    return new RedisRateLimiter(100, 200, 1);
}
```

## Rate Limiting Implementation
```java
@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {
    private final RateLimiter rateLimiter;

    public RateLimitingFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = exchange.getRequest().getRemoteAddress().getAddress()
            .getHostAddress();

        return rateLimiter.isAllowed(clientIp)
            .flatMap(allowed -> {
                if (!allowed) {
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

## Circuit Breaker Pattern
```java
@Bean
public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerConfig() {
    return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
        .circuitBreakerConfig(CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build())
        .timeLimiterConfig(TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(5))
            .build())
        .build());
}
```
