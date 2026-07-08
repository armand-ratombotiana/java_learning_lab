# Flashcards: Api Composition

## Front: What is the 
**Back:** An architectural pattern that provides structured approaches to building distributed systems with clear boundaries, standardized interactions, and incremental evolution capabilities.

## Front: What are the key benefits?
**Back:** Improved maintainability, independent deployability, fault isolation, team autonomy, and evolutionary architecture.

## Front: When should you avoid this pattern?
**Back:** Simple applications with single deployment, small teams with limited resources, early-stage products, and systems with strong consistency requirements.

## Front: What is the most common implementation mistake?
**Back:** Over-engineering by applying the pattern where simpler solutions suffice, or under-engineering by ignoring the pattern where it provides clear benefits.

## Front: What testing strategy is recommended?
**Back:** Comprehensive testing pyramid: unit tests for core logic, integration tests for component interactions, contract tests for APIs, and end-to-end tests for critical paths.

## Front: How does this pattern handle state?
**Back:** State is managed through clear boundaries: request-scoped for single operations, session-scoped for client interactions, and database-persisted for durable storage.

## Front: What security measures are important?
**Back:** Authentication at entry points, authorization at service boundaries, encryption (TLS) for all communication, secrets management, and input validation.

## Front: How does the pattern scale?
**Back:** Horizontal scaling through stateless design, vertical scaling for compute-bound tasks, caching for read-heavy workloads, and auto-scaling for demand-based capacity.

## Front: What is the role of monitoring?
**Back:** Monitoring provides visibility into system health, performance, and behavior. Key metrics include throughput, latency, error rates, and resource utilization.

## Front: How does this pattern handle failures?
**Back:** Circuit breakers prevent cascading failures. Bulkheads isolate failures. Retries with backoff handle transient failures. Graceful degradation maintains partial functionality.

## Front: What is the Strangler Fig pattern?
**Back:** An incremental migration pattern where new functionality is built alongside legacy systems. Traffic is gradually routed to the new system until the legacy system can be decommissioned.

## Front: What is the difference between orchestration and choreography?
**Back:** Orchestration uses a central coordinator to direct workflow. Choreography uses distributed events where each service reacts to events independently.

## Front: What is the Backend for Frontend pattern?
**Back:** A pattern where dedicated backend services are created for each client type (web, mobile, IoT), optimizing data shape and protocol for each specific client.

## Front: What is a sidecar proxy?
**Back:** A helper process deployed alongside the main application that handles cross-cutting concerns like service discovery, traffic management, and observability without modifying application code.

## Front: What is the Circuit Breaker pattern?
**Back:** A resilience pattern that detects failures and prevents cascading by stopping requests to failing services until they recover. States: CLOSED, OPEN, HALF_OPEN.

## Front: What is the Saga pattern?
**Back:** A pattern for managing distributed transactions through sequences of local transactions with compensating actions for rollback. Supports eventual consistency.

## Front: What is a golden path in platform engineering?
**Back:** A recommended, well-supported approach for common development tasks that reduces decision fatigue and ensures consistency across teams.

## Front: What is Backstage?
**Back:** An open-source developer portal by Spotify that provides a software catalog, templates, documentation, and self-service capabilities for internal developer platforms.

## Front: What is service mesh?
**Back:** A dedicated infrastructure layer for managing service-to-service communication. Provides traffic management, security, observability, and policy enforcement via sidecar proxies.

## Front: What is the difference between control plane and data plane?
**Back:** Control plane manages configuration and policies across the system. Data plane handles actual traffic and request processing. They are separated for security and scalability.
