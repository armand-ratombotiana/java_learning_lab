# Microservices Interview Questions

## Section 1: Microservices Fundamentals

### Q1: What are microservices? How do they differ from monoliths?
**Answer:** Microservices are small, independent services that can be deployed separately, each owning its data. Monoliths are single applications where all components are tightly coupled. Microservices offer independent deployment, technology flexibility, scalability, and fault isolation.

---

### Q2: What are the advantages and disadvantages of microservices?
**Answer:**
**Advantages:**
- Independent deployment
- Technology flexibility
- Scalability
- Fault isolation
- Team autonomy

**Disadvantages:**
- Distributed system complexity
- Data consistency challenges
- Operational overhead
- Testing complexity
- Network latency

---

### Q3: How do you decompose a monolith into microservices?
**Answer:**
1. Identify business capabilities
2. Define bounded contexts
3. Determine service boundaries
4. Design APIs
5. Plan migration strategy (strangler pattern, branch-by-abstraction)

---

### Q4: What is Database per Service pattern?
**Answer:** Each microservice owns its data and exposes it through APIs. Services don't share databases, which provides loose coupling and independence.

---

### Q5: Explain CQRS (Command Query Responsibility Segregation)
**Answer:** CQRS separates read and write operations into different models. Commands handle write operations; queries handle read operations. This optimizes performance and allows different scaling strategies.

---

## Section 2: Communication Patterns

### Q6: What are the communication styles in microservices?
**Answer:**
- **Synchronous:** REST, gRPC
- **Asynchronous:** Message queues (Kafka, RabbitMQ), Event streaming

---

### Q7: What is the Saga pattern? How does it handle distributed transactions?
**Answer:** Saga coordinates distributed transactions through a sequence of local transactions. If one step fails, compensation actions undo previous steps. Two approaches:
- **Orchestration:** Central coordinator directs flow
- **Choreography:** Services react to events

---

### Q8: What is event-driven architecture?
**Answer:** Services communicate via events rather than direct calls. Producers publish events; consumers react to them. Benefits: loose coupling, scalability, eventual consistency.

---

### Q9: How do you handle data consistency in microservices?
**Answer:**
- Saga pattern for transactional consistency
- Event sourcing for audit trail
- CQRS with materialized views
- Eventual consistency with compensation
- Consider what needs strong vs eventual consistency

---

### Q10: What is API composition?
**Answer:** Combining data from multiple services into a single response. Can be done via:
- API Gateway aggregation
- Dedicated composition service
- GraphQL federations

---

## Section 3: Service Discovery

### Q11: What is Service Discovery?
**Answer:** Mechanism for services to find each other dynamically without hardcoded addresses. Components: Service Registry (stores locations) and Client/Server lookup.

---

### Q12: Explain client-side vs server-side discovery
**Answer:**
- **Client-side:** Client queries registry and selects instance (Eureka)
- **Server-side:** Load balancer queries registry and routes (AWS ELB, Kubernetes)

---

### Q13: How does Eureka work?
**Answer:** Eureka is Netflix's service registry:
- Services register on startup
- Clients heartbeat to Eureka
- Eureka sends heartbeats to other instances
- Clients can query for service locations

---

### Q14: What is Consul?
**Answer:** HashiCorp's service discovery and configuration tool with:
- Service registry
- Health checking
- Key-value store
- Multi-datacenter support

---

## Section 4: API Gateway

### Q15: Why use an API Gateway?
**Answer:**
- Single entry point for all clients
- Request routing
- Authentication/Authorization
- Rate limiting
- Protocol translation
- Request/Response transformation

---

### Q16: What is Spring Cloud Gateway?
**Answer:** API gateway built on Spring WebFlux:
- Route-based routing
- Filters for cross-cutting concerns
- Integration with Spring Cloud
- Circuit breaker support

---

### Q17: How do you implement authentication in API Gateway?
**Answer:**
- Validate JWT tokens
- Check API keys
- OAuth2 token validation
- Forward claims to backend services
- Use gateway filter or handler

---

### Q18: What is rate limiting and how do you implement it?
**Answer:** Limits requests per client/time. Implementations:
- Token bucket algorithm
- Leaky bucket algorithm
- Fixed window / sliding window

---

## Section 5: Fault Tolerance

### Q19: What is a Circuit Breaker pattern?
**Answer:** Prevents cascading failures by:
1. **Closed:** Normal operation
2. **Open:** Fail fast after threshold
3. **Half-Open:** Test if service recovered

---

### Q20: How does circuit breaker work with fallback?
**Answer:** When circuit opens or operation fails, execute fallback instead:
```java
circuitBreaker.run(() -> callService(), 
    throwable -> getDefaultResponse());
```

---

### Q21: What are other fault tolerance patterns?
**Answer:**
- **Retry:** Retry failed requests
- **Bulkhead:** Isolate failures
- **Timeout:** Prevent hanging
- **Fallback:** Default responses
- **Health Check:** Circuit breaker uses it

---

## Section 6: Observability

### Q22: What is distributed tracing?
**Answer:** Tracking requests across service boundaries using correlation IDs. Tools: Jaeger, Zipkin, AWS X-Ray.

---

### Q23: How does Spring Cloud Sleuth work?
**Answer:** Adds trace/span IDs to logs and HTTP headers. Integrates with Zipkin for visualization.

---

### Q24: What metrics should you monitor in microservices?
**Answer:**
- Request rate, latency, error rate
- Service health
- Circuit breaker state
- Resource usage (CPU, memory)
- Business metrics

---

### Q25: How do you implement logging in microservices?
**Answer:**
- Centralized logging (ELK, Loki)
- Structured JSON logs
- Include correlation IDs
- Log levels appropriately
- Don't log sensitive data