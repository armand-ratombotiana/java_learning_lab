# Mock Interview: R2DBC (Lab 09)

**Role:** Backend Engineer (Senior)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is R2DBC and how does it differ from JDBC?

**Candidate:** R2DBC (Reactive Relational Database Connectivity) is a reactive API for database access. Key differences from JDBC:
- **Non-blocking:** JDBC blocks the calling thread for each database operation. R2DBC uses reactive streams — no thread is blocked.
- **Backpressure support:** R2DBC signals how much data the consumer can handle (Reactive Streams specification).
- **Connection pooling:** R2DBC connections are designed for asynchronous, non-blocking use.
- **Smaller thread count:** With JDBC, you need one thread per connection. With R2DBC, a few threads handle many concurrent operations.

**Interviewer:** When would you use R2DBC over JDBC/JPA?

**Candidate:** Use R2DBC when:
- Building reactive applications with Spring WebFlux (Lab 15)
- High concurrency requirements (1000+ concurrent requests)
- Streaming large result sets without blocking
- End-to-end reactive stack (WebFlux → R2DBC → Database)

Stick with JDBC/JPA when:
- Using blocking stack (Spring MVC, Tomcat)
- Need JPA features (lazy loading, caching, dirty checking)
- Simpler development model (imperative vs reactive)

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you implement database transactions with R2DBC?

**Candidate:** R2DBC transactions are managed reactively:
```java
@Transactional
public Mono<Order> createOrder(Order order) {
    return orderRepository.save(order)
        .flatMap(saved -> {
            if (saved.getTotal() > 1000) {
                return Mono.error(new BusinessException("Order too large"));
            }
            return inventoryService.reserveItems(saved.getId(), saved.getItems())
                .thenReturn(saved);
        });
}
```

Spring's `@Transactional` works with `ReactiveTransactionManager` (e.g., `R2dbcTransactionManager`). Transactions are scoped to the reactive pipeline — if any Mono in the chain errors, the transaction rolls back.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a high-throughput order processing system that handles 10K orders/second using R2DBC and WebFlux.

**Candidate:** 

**Architecture:**
```
WebFlux Controller (Netty)
    │
    ├── Validation Service (reactive)
    ├── Inventory Service (R2DBC)
    ├── Payment Service (R2DBC)
    ├── Order Repository (R2DBC)
    └── Notification Service (reactive Kafka)
```

**Reactive pipeline:**
```java
@PostMapping("/orders")
public Mono<Order> createOrder(@RequestBody @Valid Mono<OrderRequest> request) {
    return request
        .flatMap(validator::validate)
        .flatMap(inventoryService::reserveItems)
        .flatMap(this::processPayment)
        .flatMap(orderRepository::save)
        .flatMap(notificationService::sendConfirmation)
        .subscribeOn(Schedulers.parallel()); // switch to parallel scheduler for CPU work
}
```

**Connection pooling:**
```yaml
spring.r2dbc:
  url: r2dbc:postgresql://localhost:5432/orders
  pool:
    initial-size: 10
    max-size: 50
    max-idle-time: 30m
    validation-query: SELECT 1
```

With R2DBC, a pool of 50 connections can handle 10K req/sec because connections aren't blocked waiting for results. Each connection serves multiple requests asynchronously.

---

## Interviewer Feedback

**Strengths:** Clear R2DBC-JDBC comparison, good transaction understanding, practical high-throughput design  
**Areas to Improve:** Could discuss R2DBC SPI and driver implementations  
**Verdict:** Hire

---

*Databases Lab 09 MOCK_INTERVIEW.md*
