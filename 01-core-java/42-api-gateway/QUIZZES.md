# Module 42: API Gateway Pattern - Quizzes

---

## Q1: Core Purpose
What is the primary architectural purpose of an API Gateway in a microservices ecosystem?

A) To store the primary databases for all microservices.
B) To act as a single, centralized entry point that routes client requests to the appropriate backend microservices.
C) To compile Java code into Docker images.
D) To replace Kubernetes for container orchestration.

**Answer**: B
**Explanation**: An API Gateway abstracts the complex, distributed backend architecture from clients by providing a single, unified reverse proxy that handles routing and cross-cutting concerns.

---

## Q2: Cross-Cutting Concerns
Which of the following is considered a best practice to implement at the API Gateway layer rather than in every individual microservice?

A) Core business logic calculations.
B) Database schema migrations.
C) Initial JWT validation and global Rate Limiting.
D) Sending marketing emails.

**Answer**: C
**Explanation**: Cross-cutting concerns like validating the authenticity of a JWT, terminating SSL, and enforcing global rate limits are best handled once at the edge of the network (the Gateway) to avoid code duplication across 50 different microservices.

---

## Q3: Backend for Frontend (BFF)
What problem does the Backend for Frontend (BFF) pattern solve?

A) It prevents the API Gateway from becoming a bloated, single bottleneck by providing specialized, smaller gateways tailored to specific client types (e.g., Mobile vs Web).
B) It allows frontend developers to write Java code.
C) It replaces the need for a database.
D) It converts REST APIs to SOAP.

**Answer**: A
**Explanation**: A generic, one-size-fits-all API Gateway often becomes too complex. The BFF pattern creates targeted gateways designed specifically to serve the exact data requirements of distinct UIs.