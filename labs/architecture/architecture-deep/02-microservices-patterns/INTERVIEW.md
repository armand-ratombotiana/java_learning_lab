# Interview Questions — Microservices Patterns

## Q1: When should you use choreography vs orchestration sagas?
**A:** Choreography works for simple workflows with few services. Orchestration is better for complex workflows requiring centralized error handling and compensation logic.

## Q2: How does the Strangler Fig pattern work in practice?
**A:** Intercept requests at the gateway/router; route specific endpoints/features to the new microservice while keeping others on the monolith. Gradually shift traffic until the monolith is decommissioned.

## Q3: What are the trade-offs of database per service?
**A:** Pros: independent deployments, schema isolation, polyglot persistence. Cons: distributed transactions, join complexity, eventual consistency.

## Q4: How does a circuit breaker differ from retry?
**A:** Retry repeats failing calls (may worsen overload). Circuit breaker stops calls entirely when failure rate exceeds a threshold, allowing the system to recover.
