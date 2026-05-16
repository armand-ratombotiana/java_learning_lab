# Microservices Quiz

## Section 1: Microservices Patterns

**Question 1:** What is the main advantage of microservices over monolithic architecture?
- A) Simpler code
- B) Independent deployment of services
- C) Faster development
- D) Lower cost

**Answer:** B) Independent deployment of services

---

**Question 2:** What is Database per Service pattern?
- A) All services share one database
- B) Each service has its own database
- C) Services don't use databases
- D) Services use a single shared schema

**Answer:** B) Each service has its own database

---

**Question 3:** What does CQRS stand for?
- A) Common Query Return Standard
- B) Command Query Responsibility Segregation
- C) Concurrent Query Result Service
- D) Command Queue Repository Service

**Answer:** B) Command Query Responsibility Segregation

---

**Question 4:** In the Saga pattern, what is compensation?
- A) Paying for a service
- A) Rolling back previous steps when a step fails

**Answer:** B) Rolling back previous steps when a step fails

---

**Question 5:** What is API composition in microservices?
- A) Creating REST APIs
- B) Combining data from multiple services
- C) Writing API documentation
- D) Testing APIs

**Answer:** B) Combining data from multiple services

---

## Section 2: Service Discovery

**Question 6:** What is a Service Registry?
- A) A database for service data
- B) A central place where services register their locations
- C) A load balancer
- D) An API gateway

**Answer:** B) A central place where services register their locations

---

**Question 7:** What is client-side discovery?
- A) Client asks a central registry for service location
- B) Server tells client where services are
- C) No discovery is used
- D) DNS-based discovery

**Answer:** A) Client asks a central registry for service location

---

**Question 8:** Which tool is commonly used for service discovery?
- A) Docker
- B) Kubernetes
- C) Eureka or Consul
- D) Jenkins

**Answer:** C) Eureka or Consul

---

**Question 9:** What does the load balancer do in service discovery?
- A) Stores service locations
- B) Distributes requests across service instances
- C) Registers services
- D) Monitors services

**Answer:** B) Distributes requests across service instances

---

**Question 10:** What is self-registration in microservices?
- A) Manual registration by admin
- B) Services register themselves with the registry
- C) Using DNS
- D) Using configuration files

**Answer:** B) Services register themselves with the registry

---

## Section 3: API Gateway

**Question 11:** What is the main purpose of an API Gateway?
- A) Store API documentation
- B) Single entry point for all client requests
- C) Database management
- D) Service monitoring

**Answer:** B) Single entry point for all client requests

---

**Question 12:** What is rate limiting in an API Gateway?
- A) Limiting API response size
- B) Controlling number of requests per client
- C) Limiting data transfer
- D) Reducing server load

**Answer:** B) Controlling number of requests per client

---

**Question 13:** Which annotation is used in Spring Cloud Gateway?
- A) @EnableGateway
- B) @EnableRouting
- C) @EnableWebFlux
- D) No annotation needed

**Answer:** D) Spring Cloud Gateway uses RouteLocator beans

---

**Question 14:** What does circuit breaker do in API Gateway?
- A) Detects network issues
- B) Prevents cascading failures by failing fast
- C) Routes traffic
- D) Caches responses

**Answer:** B) Prevents cascading failures by failing fast

---

**Question 15:** What is a fallback in circuit breaker?
- A) Default response when circuit is open
- B) Retry mechanism
- C) Load balancing
- D) Caching

**Answer:** A) Default response when circuit is open

---

## Advanced Questions

**Question 16:** What is service mesh?
- A) A network of services
- B) Infrastructure layer handling service communication
- C) Service discovery
- D) API gateway

**Answer:** B) Infrastructure layer handling service communication

---

**Question 17:** What is the difference between orchestration and choreography in Saga?
- A) Same thing
- B) Orchestration has central controller; choreography uses events
- C) Choreography has central controller
- D) Neither uses events

**Answer:** B) Orchestration has central controller; choreography uses events

---

**Question 18:** What is eventual consistency?
- A) Data is immediately consistent
- B) Data becomes consistent over time across services
- C) Data never changes
- D) No consistency needed

**Answer:** B) Data becomes consistent over time across services

---

**Question 19:** What is a bounded context in DDD?
- A) A time limit
- B) A clear boundary where a domain model applies
- C) A test environment
- D) A service endpoint

**Answer:** B) A clear boundary where a domain model applies

---

**Question 20:** What is the benefit of distributed tracing?
- A) Faster responses
- B) Tracking requests across multiple services
- C) Better security
- D) Lower costs

**Answer:** B) Tracking requests across multiple services

---

## Score Interpretation

| Score | Level |
|-------|-------|
| 18-20 | Expert |
| 14-17 | Advanced |
| 10-13 | Intermediate |
| 5-9 | Beginner |
| < 5 | Foundation needed |