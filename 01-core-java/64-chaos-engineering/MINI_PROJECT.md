# Module 64: Chaos Engineering - Mini Project

**Project Name**: Application-Level Chaos Simulation  
**Difficulty Level**: Advanced  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Configure a Spring Boot application with "Chaos Monkey for Spring Boot" (CM4SB). Expose Actuator endpoints to dynamically trigger latency assaults and exception assaults to verify that your Circuit Breaker fallback methods function correctly under stress.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a Spring Boot Web application.
   - Add dependencies: `chaos-monkey-spring-boot`, `spring-boot-starter-actuator`, and `resilience4j-spring-boot3`.

2. **The Target Service**:
   - Create a `@Service` called `ProductService` with a method `public String getProductInfo()`.
   - The method should just return a dummy string: "Product Info API".
   - Annotate this method with `@CircuitBreaker(name = "productService", fallbackMethod = "fallbackProductInfo")`.
   - Write the fallback method that returns: "Cached Product Info".

3. **Chaos Configuration (`application.yml`)**:
   - Activate the `chaos-monkey` profile.
   - Expose the chaos monkey actuator endpoints: `management.endpoints.web.exposure.include=health,info,chaosmonkey`.
   - Enable Chaos Monkey repository/service watchers: `chaos.monkey.watcher.service=true`.

4. **The Experiment (Game Day)**:
   - Start the application.
   - Hit `GET /api/products` (should return "Product Info API").
   - Use Postman or `curl` to dynamically turn on Chaos Monkey via its Actuator POST endpoint.
   - Enable an **Exception Assault** (forcing the Service to throw a RuntimeException).
   - Hit `GET /api/products` again.
   - *Observation*: The exception is caught by Resilience4j, the circuit breaker trips, and the response is safely degraded to "Cached Product Info".

---

## 💡 Solution Blueprint

1. **Configuration**:
   ```yaml
   spring:
     profiles:
       active: chaos-monkey
   management:
     endpoints:
       web:
         exposure:
           include: "*"
   chaos:
     monkey:
       enabled: false # Start disabled, enable via API
       watcher:
         service: true
   ```

2. **The Service**:
   ```java
   @Service
   public class ProductService {
       
       @CircuitBreaker(name = "productService", fallbackMethod = "fallbackProductInfo")
       public String getProductInfo() {
           return "Real Product Info API Data";
       }
       
       public String fallbackProductInfo(Throwable t) {
           return "Fallback: Cached Product Info";
       }
   }
   ```

3. **Dynamic Chaos Injection via API**:
   ```bash
   # Enable Chaos Monkey
   curl -X POST http://localhost:8080/actuator/chaosmonkey/enable

   # Configure an Exception Assault
   curl -X POST http://localhost:8080/actuator/chaosmonkey/assaults \
   -H "Content-Type: application/json" \
   -d '{"level": 1, "exceptionsActive": true}'
   ```