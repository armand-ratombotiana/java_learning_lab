# Microservices Interview Questions

## Junior Level

### Q: Explain microservices architecture.
**A:** Microservices is an architectural style where an application is composed of small, independent services that communicate over well-defined APIs. Each service focuses on a single business capability and can be deployed, scaled, and maintained independently.

### Q: What is the difference between SOA and microservices?
**A:** SOA typically involves larger, shared services with an enterprise service bus (ESB). Microservices are smaller, independently deployable, prefer lightweight protocols (REST/messaging), and avoid shared infrastructure.

## Mid Level

### Q: How do you handle distributed transactions?
**A:** Use the Saga pattern - either choreography (each service publishes events) or orchestration (a coordinator manages the workflow). Avoid distributed transactions (2PC) in microservices due to performance and availability impacts.

### Q: Explain how you would debug a slow request across services.
**A:** Use distributed tracing (trace IDs), check each service's p99 latency, examine CPU/memory metrics, check database query performance, verify network latency, and inspect queue backlogs.

## Senior Level

### Q: How would you decompose a monolith into microservices?
**A:** 
1. Identify bounded contexts using DDD
2. Extract services incrementally using Strangler Fig pattern
3. Separate databases
4. Implement API Gateway
5. Add service discovery
6. Implement resilience patterns
7. Set up CI/CD for each service
8. Migrate traffic gradually

### Q: Design a resilient payment processing system.
**A:** 
- Idempotency keys for duplicate detection
- Circuit breaker for downstream failures
- Saga pattern for compensating actions
- Event sourcing for audit trail
- Dead letter queue for failed messages
- Retry with exponential backoff
- Monitoring and alerting on failure rates
