# API Gateway Patterns — Deep Interview Guide

## Table of Contents
1. [Gateway Patterns Overview](#gateway-patterns-overview)
2. [Spring Cloud Gateway vs Alternatives](#spring-cloud-gateway-vs-alternatives)
3. [Advanced Routing](#advanced-routing)
4. [Filter Chains](#filter-chains)
5. [Rate Limiting & Circuit Breaking](#rate-limiting--circuit-breaking)
6. [Security & Authentication](#security--authentication)
7. [Java Code Examples](#java-code-examples)
8. [15+ Interview Questions](#15-interview-questions)

---

## Gateway Patterns Overview

An API Gateway is a single entry point that routes requests to appropriate backend services, providing cross-cutting concerns in one place.

### Core Patterns

| Pattern | Description | Use Case |
|---------|-------------|----------|
| **Reverse Proxy** | Forwards client requests to backend servers | Hiding internal topology |
| **Aggregation** | Combines multiple backend responses into one response | Dashboard, mobile home screen |
| **Routing** | Routes requests based on path, header, host, weight | Microservices routing |
| **Offloading** | Removes cross-cutting concerns from services | Auth, rate limiting, logging |
| **Protocol Translation** | Converts protocols (gRPC→REST, HTTP→WebSocket) | Legacy integration |

### Gateway vs Service Mesh

| Aspect | API Gateway | Service Mesh (Istio/Linkerd) |
|--------|-------------|------------------------------|
| **Layer** | L7 (application) | L4/L7 (network proxy sidecar) |
| **Scope** | Edge/ingress traffic | Inter-service east-west traffic |
| **Features** | Auth, rate limit, routing, aggregation | mTLS, retry, traffic policy |
| **Deployment** | Single instance/fleet | Sidecar per pod |
| **Config** | Routes, predicates, filters | VirtualService, DestinationRule |
| **Use case** | External API management | Internal service communication |

### Aggregation Pattern

```java
// Aggregating results from multiple services
public Mono<AggregatedResponse> aggregate(String userId) {
    return Mono.zip(
        userClient.getUser(userId),
        orderClient.getOrders(userId),
        recommendationClient.getRecommendations(userId)
    ).map(tuple -> new AggregatedResponse(
        tuple.getT1(),
        tuple.getT2(),
        tuple.getT3()
    ));
}
```

---

## Spring Cloud Gateway vs Alternatives

### Spring Cloud Gateway (SCG)

- Built on WebFlux, Reactor, Netty
- Non-blocking, reactive throughout
- DSL-based route configuration
- Extensive filter library

### Netflix Zuul 2

- Built on Netty (async)
- Filter-based architecture (Pre, Route, Post, Error)
- Less actively maintained
- More complex configuration

### Kong (OpenResty/Lua)

- Built on OpenResty (Nginx + Lua)
- Plugin ecosystem (500+ plugins)
- DB-backed (PostgreSQL, Cassandra)
- Performance-optimized for L7

### Envoy (C++)

- Universal data plane for service mesh
- L3/L4/L7 proxy
- xDS API for dynamic config
- Best for mesh architectures

**Comparison Table:**

| Feature | SCG | Zuul 2 | Kong | Envoy |
|---------|-----|--------|------|-------|
| Async/Non-blocking | Yes | Yes | Yes | Yes |
| Java integration | Native | Native | REST/Plugin | REST/gRPC |
| Dynamic routing | Yes (DSL) | Yes | Yes (DB) | Yes (xDS) |
| Rate limiting | Redis + Custom | Built-in | Plugin | Plugin |
| Circuit breaker | Resilience4j | Hystrix | Plugin | Cluster RLS |
| WebSocket | Yes | Yes | Yes | Yes |
| Management UI | No | No | Yes (Konga) | Yes (Envoy UI) |
| Community | Large (Spring) | Declining | Large | Large (CNCF) |

---

## Advanced Routing

### Weighted Routing (Canary)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service-stable
          uri: lb://product-service
          predicates:
            - Weight=product, 90
          filters:
            - SetResponseHeader=X-Cohort, stable
        - id: product-service-canary
          uri: lb://product-service-canary
          predicates:
            - Weight=product, 10
          filters:
            - SetResponseHeader=X-Cohort, canary
```

### Header-Based Routing

```java
@Bean
public RouteLocator headerBasedRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("mobile-v2", r -> r
            .header("X-Client-Version", "2\\..*")
            .uri("lb://mobile-api-v2"))
        .route("mobile-v1", r -> r
            .header("X-Client-Version", "1\\..*")
            .uri("lb://mobile-api-v1"))
        .route("web-clients", r -> r
            .header("User-Agent", "Mozilla/.*")
            .uri("lb://web-api"))
        .build();
}
```

### Host-Based Routing (Multi-Tenant)

```java
@Bean
public RouteLocator hostBasedRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("tenant-a", r -> r
            .host("*.acme-corp.com")
            .uri("lb://acme-api"))
        .route("tenant-b", r -> r
            .host("*.globex.com")
            .uri("lb://globex-api"))
        .route("default", r -> r
            .host("**.api.example.com")
            .uri("lb://default-api"))
        .build();
}
```

### Query Parameter Routing (A/B Testing)

```java
@Bean
public RouteLocator queryRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("experiment-group-a", r -> r
            .query("experiment", "group-a")
            .uri("lb://new-recommendation-engine"))
        .route("control-group", r -> r
            .query("experiment", "control")
            .uri("lb://recommendation-engine"))
        .build();
}
```

### Cookie-Based Routing (Session Affinity)

```java
@Bean
public RouteLocator cookieRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("sticky-session", r -> r
            .cookie("SESSION_ID", "node-.*")
            .uri("lb://sticky-backend"))
        .build();
}
```

---

## Filter Chains

### Filter Lifecycle in Spring Cloud Gateway

```
Client Request
    │
    ├── Pre-Filters (modify request)
    │   ├── AddRequestHeader, AddRequestParameter
    │   ├── PrefixPath, RewritePath
    │   ├── CircuitBreaker (before route)
    │   └── RateLimiter
    │
    ├── Route Filter (send to backend)
    │
    ├── Post-Filters (modify response)
    │   ├── AddResponseHeader
    │   ├── ModifyResponseBody
    │   └── CircuitBreaker (fallback on error)
    │
    └── Response to Client
```

### Built-in Filter Categories

| Category | Filters | Purpose |
|----------|---------|---------|
| **Header** | `AddRequestHeader`, `RemoveRequestHeader`, `SetRequestHeader` | Modify headers |
| **Path** | `PrefixPath`, `RewritePath`, `StripPrefix`, `SetPath` | Path manipulation |
| **Query** | `AddRequestParameter`, `RemoveRequestParameter` | Query params |
| **Body** | `ModifyRequestBody`, `ModifyResponseBody` | Body transformation |
| **Security** | `SetStatus`, `RedirectTo`, `Retry` | Error handling |
| **Circuit Breaker** | `CircuitBreaker` | Resilience |
| **Rate Limiter** | `RequestRateLimiter` | Throttling |
| **Resilience** | `Retry`, `CircuitBreaker` | Fault tolerance |

### Filter Ordering

Filters implement `Ordered` interface. Default ordering for built-in filters:

```
-5  CircuitBreaker (before routing)
-4  RequestRateLimiter
-3  Retry
-2  PrefixPath/StripPrefix
-1  AddRequestHeader/AddRequestParameter
 0  RouteToRequestUrl
+1  ModifyResponseBody
+2  AddResponseHeader
+5  CircuitBreaker (after routing/fallback)
```

### Custom GatewayFilterFactory

```java
@Component
public class RequestTimingGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RequestTimingGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(RequestTimingGatewayFilterFactory.class);

    public RequestTimingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            long startTime = System.nanoTime();
            String path = exchange.getRequest().getURI().getPath();

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long elapsed = System.nanoTime() - startTime;
                if (config.isLogSlowRequests() && elapsed > config.getThresholdNanos()) {
                    log.warn("Slow request [{}] took {}ms", path, elapsed / 1_000_000);
                }
                exchange.getAttributes().put("requestTimeMs", elapsed / 1_000_000);
            }));
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("thresholdNanos", "logSlowRequests");
    }

    public static class Config {
        private long thresholdNanos = Duration.ofSeconds(1).toNanos();
        private boolean logSlowRequests = true;

        public long getThresholdNanos() { return thresholdNanos; }
        public void setThresholdNanos(long thresholdNanos) { this.thresholdNanos = thresholdNanos; }
        public boolean isLogSlowRequests() { return logSlowRequests; }
        public void setLogSlowRequests(boolean logSlowRequests) { this.logSlowRequests = logSlowRequests; }
    }
}
```

Usage in routes:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: slow-request-monitor
          uri: lb://backend
          predicates:
            - Path=/api/**
          filters:
            - name: RequestTiming
              args:
                thresholdNanos: 500000000  # 500ms
                logSlowRequests: true
```

### GlobalFilter (Applies to All Routes)

```java
@Component
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders()
            .getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        ServerWebExchange mutatedExchange = exchange.mutate()
            .request(r -> r.header(CORRELATION_ID_HEADER, correlationId))
            .response(r -> r.header(CORRELATION_ID_HEADER, correlationId))
            .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -100; // Run first
    }
}
```

---

## Rate Limiting & Circuit Breaking

### Rate Limiting Strategies

#### Token Bucket (Redis-based)

```java
@Bean
public RedisRateLimiter redisRateLimiter() {
    // replenishRate: tokens/sec added to bucket
    // burstCapacity: max token accumulation
    // requestedTokens: tokens consumed per request
    return new RedisRateLimiter(100, 200, 1);
}
```

#### Per-User Rate Limiting

```java
@Component
public class UserKeyResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return exchange.getPrincipal()
            .map(Principal::getName)
            .defaultIfEmpty(exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress());
    }
}
```

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-rate-limited
          uri: lb://backend
          predicates:
            - Path=/api/**
          filters:
            - name: RequestRateLimiter
              args:
                key-resolver: "#{@userKeyResolver}"
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

#### IP-Based Rate Limiting

```java
@Component("ipKeyResolver")
public class IpKeyResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        String ip = exchange.getRequest().getHeaders()
            .getFirst("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = Objects.requireNonNull(
                exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
        }
        return Mono.just(ip.split(",")[0].trim());
    }
}
```

### Circuit Breaker at Gateway Level

```java
@Bean
public RouteLocator cbRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("resilient-service", r -> r
            .path("/api/resilient/**")
            .filters(f -> f
                .circuitBreaker(config -> config
                    .setName("backendCB")
                    .setFallbackUri("forward:/fallback/default")
                    .setStatusCodes("500", "502", "503", "504"))
                .retry(config -> config
                    .setRetries(3)
                    .setStatuses(HttpStatus.BAD_GATEWAY)
                    .setMethods(HttpMethod.GET)
                    .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(5), 2, true)))
            .uri("lb://backend-service"))
        .build();
}
```

### Combining Rate Limiter + Circuit Breaker + Retry

```java
@Bean
public RouteLocator resilientRoute(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("order-service-resilient", r -> r
            .path("/api/orders/**")
            .filters(f -> f
                .requestRateLimiter(c -> c
                    .setRateLimiter(redisRateLimiter())
                    .setKeyResolver(userKeyResolver))
                .circuitBreaker(c -> c
                    .setName("orderCB")
                    .setFallbackUri("forward:/api/orders/fallback")
                    .setStatusCode("GATEWAY_TIMEOUT"))
                .retry(c -> c
                    .setRetries(3)
                    .setSeries(ServerWebExchange.ServerErrorSeries.SERVER_ERROR)
                    .setMethods(HttpMethod.GET))
                .addResponseHeader("X-Resilience", "active"))
            .uri("lb://order-service"))
        .build();
}
```

### Custom RateLimiter Implementation

```java
@Component
public class InMemoryRateLimiter extends AbstractRateLimiter<InMemoryRateLimiter.Config> {

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public static final String CONFIGURATION_PROPERTY_NAME = "in-memory-rate-limiter";
    
    public InMemoryRateLimiter() {
        super(Config.class, CONFIGURATION_PROPERTY_NAME, null);
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        Config config = getConfig().get(routeId);
        if (config == null) {
            return Mono.just(new Response(true, Map.of()));
        }
        TokenBucket bucket = buckets.computeIfAbsent(id,
            k -> new TokenBucket(config.replenishRate, config.burstCapacity));
        return Mono.just(new Response(bucket.tryConsume(), Map.of(
            "Remaining", String.valueOf(bucket.getTokens()),
            "Reset", String.valueOf(bucket.getResetTime())
        )));
    }

    @Override
    public Map<String, Config> getConfig() {
        return super.getConfig();
    }

    static class TokenBucket {
        private final double maxTokens;
        private final double refillRate;
        private double tokens;
        private long lastRefill;

        TokenBucket(double refillRate, double maxTokens) {
            this.refillRate = refillRate;
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.lastRefill = System.nanoTime();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }

        synchronized void refill() {
            long now = System.nanoTime();
            double elapsed = (now - lastRefill) / 1_000_000_000.0;
            tokens = Math.min(maxTokens, tokens + elapsed * refillRate);
            lastRefill = now;
        }

        double getTokens() { return tokens; }
        long getResetTime() {
            return lastRefill + (long)((1.0 - tokens) * 1_000_000_000 / refillRate);
        }
    }

    public static class Config {
        private int replenishRate = 10;
        private int burstCapacity = 20;

        public int getReplenishRate() { return replenishRate; }
        public void setReplenishRate(int replenishRate) { this.replenishRate = replenishRate; }
        public int getBurstCapacity() { return burstCapacity; }
        public void setBurstCapacity(int burstCapacity) { this.burstCapacity = burstCapacity; }
    }
}
```

---

## Security & Authentication

### JWT Validation at Gateway

```java
@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private final ReactiveJwtDecoder jwtDecoder;

    public JwtAuthenticationGlobalFilter(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        return jwtDecoder.decode(token)
            .flatMap(jwt -> {
                ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r.header("X-User-Id", jwt.getSubject())
                        .header("X-User-Roles",
                            String.join(",", jwt.getClaimAsStringList("roles")))
                        .header("X-User-Tenant", jwt.getClaimAsString("tenant")))
                    .build();
                return chain.filter(mutated);
            })
            .onErrorResume(JwtException.class, e -> unauthorized(exchange, "Invalid JWT"));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
            .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() {
        return -200; // Before rate limiting
    }
}
```

---

## Java Code Examples

### 1. Complete Gateway Configuration

```java
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator highLevelRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("orders", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .circuitBreaker(c -> c.setName("ordersCB")
                        .setFallbackUri("forward:/fallback/orders"))
                    .retry(3)
                    .requestRateLimiter(c -> c.setRateLimiter(
                        new RedisRateLimiter(50, 100, 1)))
                    .prefixPath("/v2"))
                .uri("lb://order-service"))
            .route("products", r -> r
                .host("api.example.com")
                .and().path("/products/**")
                .filters(f -> f
                    .addRequestHeader("X-Source", "gateway")
                    .rewritePath("/products/(?<id>.*)", "/api/v1/products/${id}"))
                .uri("lb://product-service"))
            .route("websocket", r -> r
                .path("/ws/**")
                .uri("ws://notification-service"))
            .build();
    }
}
```

### 2. Custom GatewayFilterFactory — Rate Limiting Logger

```java
@Component
public class RateLimitLoggingGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RateLimitLoggingGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(RateLimitLoggingGatewayFilterFactory.class);

    public RateLimitLoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String routeId = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpStatus status = exchange.getResponse().getStatusCode();
                if (status == HttpStatus.TOO_MANY_REQUESTS) {
                    log.warn("Rate limit exceeded for route [{}]: {} {}",
                        routeId,
                        exchange.getRequest().getMethod(),
                        exchange.getRequest().getURI().getPath());
                    if (config.isTrackMetrics()) {
                        // Increment counter in metrics registry
                    }
                }
            }));
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("trackMetrics");
    }

    public static class Config {
        private boolean trackMetrics = true;
        public boolean isTrackMetrics() { return trackMetrics; }
        public void setTrackMetrics(boolean trackMetrics) { this.trackMetrics = trackMetrics; }
    }
}
```

### 3. Gateway Health Check Aggregator

```java
@Component
public class HealthAggregatingGatewayFilterFactory
        extends AbstractGatewayFilterFactory<HealthAggregatingGatewayFilterFactory.Config> {

    private final WebClient webClient;

    public HealthAggregatingGatewayFilterFactory(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClient = webClientBuilder.build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getURI().getPath().equals("/api/health")) {
                return chain.filter(exchange);
            }
            return aggregateHealth(config.getServices())
                .flatMap(health -> {
                    exchange.getResponse().setStatusCode(HttpStatus.OK);
                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    byte[] body = health.getBytes(StandardCharsets.UTF_8);
                    return exchange.getResponse()
                        .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
                });
        };
    }

    private Mono<String> aggregateHealth(List<String> services) {
        return Flux.fromIterable(services)
            .flatMap(service -> webClient.get()
                .uri("lb://" + service + "/actuator/health")
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> "\"" + service + "\":" + body)
                .onErrorReturn("\"" + service + "\":{\"status\":\"DOWN\"}"))
            .collectList()
            .map(results -> "{\"status\":\"UP\",\"services\":{" + String.join(",", results) + "}}");
    }

    public static class Config {
        private List<String> services = List.of();
        public List<String> getServices() { return services; }
        public void setServices(List<String> services) { this.services = services; }
    }
}
```

### 4. Pre-Filter for Request Validation

```java
@Component
public class RequestValidationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RequestValidationGatewayFilterFactory.Config> {

    public RequestValidationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Validate required headers
            for (String required : config.getRequiredHeaders()) {
                if (!request.getHeaders().containsKey(required)) {
                    return errorResponse(exchange, "Missing required header: " + required,
                        HttpStatus.BAD_REQUEST);
                }
            }

            // Validate content type for POST/PUT
            if (request.getMethod() == HttpMethod.POST || request.getMethod() == HttpMethod.PUT) {
                String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
                if (contentType == null || !config.getAllowedContentTypes().contains(contentType)) {
                    return errorResponse(exchange,
                        "Content-Type must be one of: " + config.getAllowedContentTypes(),
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE);
                }
            }

            // Validate path parameters
            Map<String, String> pathParams = exchange.getAttribute(
                ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (pathParams != null) {
                for (String requiredParam : config.getRequiredPathParams()) {
                    if (!pathParams.containsKey(requiredParam)) {
                        return errorResponse(exchange,
                            "Missing required path parameter: " + requiredParam,
                            HttpStatus.BAD_REQUEST);
                    }
                }
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> errorResponse(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        String body = "{\"title\":\"Validation Error\",\"detail\":\"" + message + "\",\"status\":" + status.value() + "}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
            .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    public static class Config {
        private List<String> requiredHeaders = List.of();
        private List<String> requiredPathParams = List.of();
        private List<String> allowedContentTypes = List.of("application/json");

        public List<String> getRequiredHeaders() { return requiredHeaders; }
        public void setRequiredHeaders(List<String> requiredHeaders) { this.requiredHeaders = requiredHeaders; }
        public List<String> getRequiredPathParams() { return requiredPathParams; }
        public void setRequiredPathParams(List<String> requiredPathParams) { this.requiredPathParams = requiredPathParams; }
        public List<String> getAllowedContentTypes() { return allowedContentTypes; }
        public void setAllowedContentTypes(List<String> allowedContentTypes) { this.allowedContentTypes = allowedContentTypes; }
    }
}
```

---

## 15+ Interview Questions

### Basic

1. **What is the difference between an API Gateway and a reverse proxy?** — A reverse proxy simply forwards requests. An API Gateway adds cross-cutting concerns (auth, rate limiting, aggregation, circuit breaking) on top of routing.

2. **How does Spring Cloud Gateway differ from Zuul?** — SCG is reactive (WebFlux/Netty), Zuul 1 is blocking (Servlet). Zuul 2 is async but less maintained. SCG has a modern DSL and better integration with Spring ecosystem.

3. **What are route predicates in Spring Cloud Gateway?** — Conditions that determine whether a route matches a request: Path, Header, Method, Query, Cookie, Host, Weight, RemoteAddr, etc.

### Intermediate

4. **Explain the filter chain in Spring Cloud Gateway.** — Filters execute in order: pre-filters (before routing) → route → post-filters (after routing). GlobalFilter applies to all routes; GatewayFilterFactory is route-specific.

5. **How do you implement canary deployments with Spring Cloud Gateway?** — Use Weight predicate. Route X% to stable, Y% to canary. Combine with Header predicate for internal tester access to canary.

6. **What is the difference between GatewayFilter and GlobalFilter?** — GatewayFilter is route-specific (configured per-route in DSL). GlobalFilter applies to all routes. Both implement the same `filter()` contract.

7. **How does Redis rate limiting work in the gateway?** — Token bucket algorithm. Redis keys store token count and timestamp. `replenishRate` = tokens/sec. `burstCapacity` = max burst. `requestedTokens` = cost per request.

8. **How do you handle fallback responses from the circuit breaker in the gateway?** — `setFallbackUri("forward:/fallback/...")`. The fallback URI is routed internally to a local handler that returns a degraded response.

### Advanced

9. **Design a high-availability API Gateway deployment.** — Multiple gateway instances behind a load balancer (ALB/Nginx). Stateless design: session state in Redis. Rate limiting in Redis. Routes in Config Server or DB. Health checks for auto-scaling.

10. **How do you implement request aggregation in the gateway?** — Create a custom `GatewayFilterFactory` that calls `WebClient` to multiple downstream services, combines responses with `Mono.zip`, and returns the aggregated result.

11. **Explain the Gateway's integration with Spring Security.** — Use `SecurityWebFilterChain` to secure routes. OAuth2 resource server can validate JWTs at the gateway level. `ServerWebExchange` carries authentication context to downstream services via headers.

12. **How do you handle large file uploads through the gateway?** — Configure `spring.codec.max-in-memory-size` and `spring.webflux.multipart.max-disk-usage-per-part`. Use streaming body filters. Avoid buffering entire file in memory.

13. **Design a multi-region API Gateway strategy with failover.** — Active-active: DNS routes to nearest region. Each region has its own gateway fleet. Cross-region failover uses health checks. Config synchronized via Config Server or Git.

14. **How do you implement gRPC-web translation in the gateway?** — Spring Cloud Gateway doesn't natively support gRPC. Use Envoy as a sidecar to translate gRPC-web to gRPC. Or implement a custom filter that converts between HTTP/1.1 JSON and HTTP/2 protobuf.

15. **Compare Spring Cloud Gateway vs Kong for enterprise use.** — SCG: Best for Java/Spring shops, deep Spring integration, reactive. Kong: Best for polyglot teams, DB-backed config, plugin marketplace, built-in developer portal.

16. **How do you implement dynamic route reloading without restart?** — Use `RouteLocator` backed by a DB or Config Server. `ApplicationEventPublisher` triggers `RefreshRoutesEvent`. `CachingRouteLocator` clears cache and reloads routes.

17. **Explain the ModifiyRequestBody filter and when you'd use it.** — Allows modifying request body before routing. Use cases: encryption, schema transformation, adding default fields. Must buffer body first.

18. **How do you troubleshoot a "429 Too Many Requests" from the gateway?** — Check Redis rate limiter keys: `KEYS rate_limiter.*`. Verify key resolver extracts correct identifier. Check `replenishRate` vs actual traffic. Increase `burstCapacity` for spikes.