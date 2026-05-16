# Architecture Patterns - QUIZ

## Quiz Instructions
This quiz contains 30 questions covering layered architecture, microservices, event-driven architecture, and CQRS. Each question has 4 options with one correct answer.

---

## Questions 1-10: Layered Architecture

**Q1. Which layer in a layered architecture is responsible for handling HTTP requests and responses?**
- A) Business Layer
- B) Data Layer
- C) Presentation Layer
- D) Integration Layer

**Answer: C** - The Presentation Layer handles HTTP requests/responses, typically through controllers and API endpoints.

---

**Q2. What is the main principle of strict layering?**
- A) All layers can access any other layer
- B) Each layer can only access the layer directly below it
- C) Layers can skip levels to access data directly
- D) Only the presentation layer can access business layer

**Answer: B** - Strict layering means each layer can only access the layer directly below it, ensuring clean separation of concerns.

---

**Q3. In a 3-tier architecture, which component handles business rules and logic?**
- A) Presentation Tier
- B) Business Tier
- C) Data Tier
- D) Integration Tier

**Answer: B** - The Business Tier contains all business logic, rules, and domain logic.

---

**Q4. What is the main advantage of layered architecture?**
- A) Maximum performance
- B) Easy to understand and test
- C) Supports microservices
- D) Event-driven communication

**Answer: B** - Layered architecture is easy to understand and test due to its clear separation of concerns.

---

**Q5. Which pattern is typically used in the Data Layer?**
- A) Service Locator
- B) Repository Pattern
- C) Front Controller
- D) Decorator Pattern

**Answer: B** - The Repository Pattern is commonly used in the Data Layer to abstract data access.

---

**Q6. What is a disadvantage of layered architecture?**
- A) Easy testing
- B) Clear responsibilities
- C) Performance overhead from multiple layers
- D) Simple to implement

**Answer: C** - Each layer adds latency, creating performance overhead compared to flatter architectures.

---

**Q7. In a layered architecture, where should validation logic be placed?**
- A) Only in Presentation Layer
- B) Only in Data Layer
- C) Both Presentation and Business Layers
- D) Only in Business Layer

**Answer: C** - Validation should occur in both layers: basic validation in Presentation, business rules in Business Layer.

---

**Q8. What is the purpose of DTOs (Data Transfer Objects)?**
- A) Store data in database
- B) Transfer data between layers
- C) Define business logic
- D) Handle HTTP headers

**Answer: B** - DTOs transfer data between layers, especially between presentation and business layers.

---

**Q9. Which annotation is typically used to define a REST controller in Spring?**
- A) @Service
- B) @Repository
- C) @RestController
- D) @Component

**Answer: C** - @RestController is used to define REST API endpoints in Spring applications.

---

**Q10. What happens when a lower layer directly accesses a higher layer?**
- A) This is recommended for better performance
- B) It violates the layered architecture principle
- C) It is allowed in special cases
- D) It has no effect on the architecture

**Answer: B** - Direct access from a lower to a higher layer violates the layered architecture principle.

---

## Questions 11-20: Microservices

**Q11. What is the single responsibility principle in microservices?**
- A) Each service should have one database
- B) Each service should own one business capability
- C) Each service should handle one HTTP request
- D) Each service should have one team

**Answer: B** - Each microservice should own and be responsible for one specific business capability.

---

**Q12. Which component is used for service discovery in microservices?**
- A) Load Balancer
- B) API Gateway
- C) Service Registry (e.g., Eureka, Consul)
- D) Message Broker

**Answer: C** - Service Registry maintains a registry of available service instances for dynamic discovery.

---

**Q13. What is the main purpose of an API Gateway?**
- A) Store application data
- B) Route requests to appropriate services and handle cross-cutting concerns
- C) Process business logic
- D) Manage database connections

**Answer: B** - API Gateway routes requests to services and handles concerns like authentication, rate limiting.

---

**Q14. Which communication pattern provides the highest coupling?**
- A) Asynchronous messaging
- B) REST/gRPC calls
- C) Event-driven
- D) Message queues

**Answer: B** - Synchronous REST/gRPC calls provide the highest coupling as services directly depend on each other.

---

**Q15. What is circuit breaker pattern used for?**
- A) Balance load across servers
- B) Prevent cascading failures between services
- C) Cache frequently accessed data
- D) Manage database transactions

**Answer: B** - Circuit breaker prevents cascading failures by stopping requests to failing services.

---

**Q16. In microservices, what does "smart endpoints, dumb pipes" mean?**
- A) All logic should be in the pipes
- B) Business logic in services, not in infrastructure
- C) Use only HTTP for communication
- D) Avoid using any communication framework

**Answer: B** - Services contain business logic while communication infrastructure (pipes) simply moves messages.

---

**Q17. What is a disadvantage of microservices architecture?**
- A) Independent scaling
- B) Technology heterogeneity
- C) Data consistency challenges
- D) Team autonomy

**Answer: C** - Managing data consistency across services is challenging due to distributed nature.

---

**Q18. Which tool is commonly used for distributed tracing?**
- A) Kafka
- B) Prometheus
- C) Zipkin/Jaeger
- D) Docker

**Answer: C** - Zipkin and Jaeger are popular distributed tracing tools for observing service interactions.

---

**Q19. What is the purpose of a configuration server in microservices?**
- A) Store service data
- B) Centralize configuration management
- C) Balance load
- D) Route requests

**Answer: B** - Configuration server centralizes all service configurations, enabling dynamic updates.

---

**Q20. How do microservices typically handle data ownership?**
- A) Shared database across all services
- B) Each service owns its data and database
- C) No databases in microservices
- D) Single database for all services

**Answer: B** - Each microservice owns its data and database, following the database-per-service pattern.

---

## Questions 21-30: Event-Driven & CQRS

**Q21. What is the main characteristic of event-driven architecture?**
- A) Direct synchronous API calls
- B) Components communicate by emitting and responding to events
- C) All components share a single database
- D) Services call each other directly

**Answer: B** - In EDA, components communicate by emitting and responding to events rather than direct calls.

---

**Q22. Which is an example of a domain event?**
- A) HTTP request received
- B) Database connection established
- C) OrderPlaced
- D) Log entry written

**Answer: C** - OrderPlaced is a domain event representing a significant business occurrence.

---

**Q23. What does eventual consistency mean in event-driven systems?**
- A) All updates happen at the same time
- B) Updates propagate over time, system may be temporarily inconsistent
- C) No consistency is required
- D) Consistency is guaranteed immediately

**Answer: B** - Eventual consistency means updates propagate over time, so the system may be temporarily out of sync.

---

**Q24. What is CQRS?**
- A) Common Query Responsibility Standard
- B) Command Query Responsibility Segregation
- C) Concurrent Query Result System
- D) Centralized Queue Request Service

**Answer: B** - CQRS separates read and write operations into different models.

---

**Q25. In CQRS, which side handles Create, Update, Delete operations?**
- A) Query Side
- B) Command Side
- C) Both sides
- D) Neither side

**Answer: B** - The Command Side handles all write operations (Create, Update, Delete).

---

**Q26. What is event sourcing?**
- A) Storing current state only
- B) Storing all state changes as a sequence of events
- C) Using events as data sources
- D) Eliminating the need for databases

**Answer: B** - Event sourcing stores all state changes as an immutable sequence of events.

---

**Q27. What is a projection in CQRS?**
- A) A way to project code to production
- B) A read model built from events
- C) A type of command handler
- D) A database view

**Answer: B** - A projection builds and maintains read models by processing events.

---

**Q28. Why is idempotency important in event processing?**
- A) To improve performance
- B) To handle duplicate events gracefully
- C) To reduce memory usage
- D) To simplify event schema

**Answer: B** - Idempotency ensures duplicate events don't cause incorrect results in processing.

---

**Q29. What is the main advantage of separating read and write models in CQRS?**
- A) Simpler code
- B) Optimized performance for each operation type
- C) Single database
- D) Reduced complexity

**Answer: B** - Separate models allow optimization of read and write operations independently for better performance.

---

**Q30. In an event-driven system, what is a pub/sub pattern?**
- A) One producer, one consumer
- B) One producer, multiple consumers
- C) Multiple producers, one consumer
- D) No producers or consumers

**Answer: B** - Pub/sub allows one producer to broadcast events to multiple interested consumers.

---

## Answer Key

| Question | Answer | Question | Answer | Question | Answer |
|----------|--------|----------|--------|----------|--------|
| 1 | C | 11 | B | 21 | B |
| 2 | B | 12 | C | 22 | C |
| 3 | B | 13 | B | 23 | B |
| 4 | B | 14 | B | 24 | B |
| 5 | B | 15 | B | 25 | B |
| 6 | C | 16 | B | 26 | B |
| 7 | C | 17 | C | 27 | B |
| 8 | B | 18 | C | 28 | B |
| 9 | C | 19 | B | 29 | B |
| 10 | B | 20 | B | 30 | B |

---

## Score Interpretation

- **27-30**: Expert - Deep understanding of all patterns
- **21-26**: Proficient - Good grasp of concepts
- **15-20**: Developing - Need more study
- **Below 15**: Review material and try again