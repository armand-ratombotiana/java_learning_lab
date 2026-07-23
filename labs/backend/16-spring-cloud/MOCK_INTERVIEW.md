# Mock Interview: Spring Cloud (Lab 16)

**Role:** Backend Engineer (Senior/Staff)  
**Duration:** 50 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What problems does Spring Cloud solve in a microservices architecture?

**Candidate:** Spring Cloud provides solutions for distributed systems challenges:
1. **Service Discovery** (Eureka) — how services find each other
2. **Configuration Management** (Config Server) — externalized configuration across services
3. **Load Balancing** (Spring Cloud LoadBalancer) — client-side load balancing across instances
4. **Circuit Breakers** (Resilience4J) — fault tolerance when dependencies fail
5. **API Gateway** (Spring Cloud Gateway) — unified entry point with routing, filtering, rate limiting
6. **Distributed Tracing** (Sleuth + Micrometer) — request correlation across services
7. **Bus** (Spring Cloud Bus) — propagating changes across distributed nodes

**Interviewer:** How does Eureka service discovery work?

**Candidate:** Eureka follows a client-server model. Each microservice registers with the Eureka server on startup, sending its host, port, health status, and metadata. Other services query Eureka to discover registered instances. Eureka clients cache the registry locally and refresh periodically. If a service misses 3 heartbeats (default 30s intervals), Eureka evicts it. The client-side caching means even if Eureka server is temporarily unavailable, services can still route based on cached data.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does the circuit breaker pattern work with Resilience4J?

**Candidate:** Resilience4J's circuit breaker has three states:
1. **Closed:** Normal operation. Requests pass through. Failed requests increment a failure counter.
2. **Open:** When failure rate exceeds threshold (default 50% in last 100 requests). Requests fail immediately with `CallNotPermittedException`.
3. **Half-Open:** After wait duration (default 60s). A configurable number of test requests are allowed. If they succeed, circuit closes. If they fail, circuit re-opens.

```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
public Payment processPayment(Order order) {
    return paymentClient.charge(order);
}

public Payment paymentFallback(Order order, Throwable t) {
    log.warn("Payment service unavailable, using fallback", t);
    return new Payment(order.getId(), PaymentStatus.PENDING_RETRY);
}
```

**Key configuration:**
```yaml
resilience4j.circuitbreaker:
  instances:
    paymentService:
      slidingWindowSize: 100
      failureRateThreshold: 50
      waitDurationInOpenState: 60s
      permittedNumberOfCallsInHalfOpenState: 10
      minimumNumberOfCalls: 10
```

---

## Round 3: Hard (15-25 minutes)

**Interviewer:** Design a complete microservices infrastructure with Spring Cloud for an e-commerce platform with 99.99% uptime requirement.

**Candidate:** 

**Architecture:**
```
                           ┌─────────────┐
                           │ Config       │
                           │ Server (HA)  │
                           └──────┬──────┘
                                  │ pulls config
                 ┌────────────────┼────────────────┐
                 │                │                 │
         ┌───────┴───────┐  ┌────┴────┐   ┌────────┴────────┐
         │ Spring Cloud   │  │ Eureka  │   │ Spring Cloud    │
         │ Gateway (x3)   │  │ Server  │   │ Sleuth + Zipkin │
         ├───┬───┬───┬───┤  │ (x3)    │   │ Tracing          │
         │   │   │   │   │  └─────────┘   └─────────────────┘
         │  /api/orders  │
         │  /api/products│
         │  /api/payments│
         └───┼───┼───┼──┘
             │   │   │
   ┌─────────┘   │   └──────────┐
   ▼             ▼              ▼
Order Svc    Product Svc    Payment Svc
  │   │         │   │          │   │
  │   │  Resilience4J circuit breakers
  │   ▼         │   ▼          │   ▼
  │  Redis      │  Redis       │  Redis
  ▼             ▼              ▼
PostgreSQL    PostgreSQL    PostgreSQL
```

**High-availability setup:**
- Eureka server in cluster mode (3 nodes, each replicates to others)
- Config Server backed by Git backend, multiple instances behind load balancer
- API Gateway horizontally scaled (x3 minimum)
- Each microservice with at least 3 instances
- Circuit breakers for all inter-service calls
- Rate limiting per client at Gateway level with Redis
- Distributed tracing with Micrometer Tracing + Zipkin

**Disaster recovery:**
- Multi-region deployment (active-active or active-passive)
- Config Server replicates to both regions
- Database cross-region replication (CDC-based)
- DNS-based failover

**Interviewer:** How would you handle a scenario where the Config Server is down during deployment?

**Candidate:** Never have the Config Server as a hard dependency for startup! Best practices:
1. **Local fallback:** Each service has a `bootstrap.yml` with local defaults. Config Server values override these.
2. **Retry on startup:** `spring.cloud.config.retry.*` settings — exponential backoff on Config Server fetch failure
3. **Health check integration:** Config Server health is a liveness probe dependency
4. **Fail-fast disabled:** `spring.cloud.config.fail-fast=false` — service starts with local config even if Config Server is unreachable
5. **Cached configuration:** Spring Cloud Bus + `@RefreshScope` beans can cache last known good configuration
6. **Config file in volume:** For K8s deployments, mount ConfigMap as a local file with the same configuration

The rule: **Config Server enhances, never blocks, service startup.**

---

## Interviewer Feedback

**Strengths:** Excellent infrastructure design, practical HA patterns, strong resilience knowledge  
**Areas to Improve:** Could discuss Spring Cloud Gateway WebFlux performance optimizations  
**Verdict:** Strong Hire

---

*Lab 16 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
