# Module 42: API Gateway Pattern - Mini Project

**Project Name**: Edge Router and Security Gateway  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Use **Spring Cloud Gateway** to build a functional API Gateway that dynamically routes traffic to backend microservices, modifies HTTP headers on the fly, and implements a global Rate Limiter using Redis to protect downstream services from abuse.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a Spring Boot application with dependencies: `spring-cloud-starter-gateway`, `spring-boot-starter-data-redis-reactive`, and `spring-boot-starter-actuator`.
   - *Note*: Spring Cloud Gateway relies on Spring WebFlux (Netty). Do NOT include `spring-boot-starter-web` (Tomcat), as it will cause conflicts.

2. **Basic Routing Configuration (`application.yml`)**:
   - Configure a route named `inventory_route`.
   - Any request matching the path `/api/inventory/**` should be routed to a dummy backend URL (e.g., `https://httpbin.org/get` or a local mock service).
   - Use a **Filter** to strip the prefix, so `/api/inventory/123` becomes `/123` when it hits the backend.

3. **Request/Response Modification Filters**:
   - Add another filter to the `inventory_route` that automatically adds a header `X-Gateway-Trace-Id` with a UUID to the outbound request.
   - Add a filter that adds a header `X-Response-Time` to the inbound response going back to the client.

4. **Global Rate Limiting (Redis)**:
   - Configure a `RequestRateLimiter` filter for a new route called `billing_route`.
   - Use the `RedisRateLimiter` to allow a maximum of 2 requests per second (replenish rate), with a burst capacity of 5.
   - You must define a `KeyResolver` Bean in a Java configuration class. Create a `KeyResolver` that limits traffic based on the client's IP address (extracted from the request).

5. **Testing the Gateway**:
   - Start a local Redis instance (e.g., via Docker).
   - Send requests to the gateway path. Verify the headers are injected.
   - Spam the `/api/billing/**` route and verify that after 5 rapid requests, the gateway rejects traffic with an `HTTP 429 Too Many Requests` status code.

---

## 💡 Solution Blueprint

1. **Java Configuration (Rate Limiting Key Resolver)**:
   ```java
   @Configuration
   public class GatewayConfig {
       
       // Limits requests based on the client's IP address
       @Bean
       public KeyResolver ipKeyResolver() {
           return exchange -> Mono.just(
               exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
           );
       }
   }
   ```

2. **YAML Routing & Filter Configuration**:
   ```yaml
   spring:
     cloud:
       gateway:
         routes:
           # Basic Routing and Header Modification
           - id: inventory_route
             uri: https://httpbin.org
             predicates:
               - Path=/api/inventory/**
             filters:
               - StripPrefix=2
               - AddRequestHeader=X-Gateway-Source, Spring-Cloud-Gateway
               
           # Rate Limited Route
           - id: billing_route
             uri: https://httpbin.org
             predicates:
               - Path=/api/billing/**
             filters:
               - name: RequestRateLimiter
                 args:
                   redis-rate-limiter.replenishRate: 2
                   redis-rate-limiter.burstCapacity: 5
                   key-resolver: "#{@ipKeyResolver}"
   ```