# Module 22: Microservices Concepts - Quizzes

---

## Q1: Microservices Data Management
What is the recommended approach for data management in a microservices architecture?

A) A single, highly optimized relational database for all services.
B) Database per Service (each service manages its own database schema).
C) Storing all data in memory without persistent storage.
D) Using a shared file system.

**Answer**: B
**Explanation**: To ensure loose coupling and independent deployability, each microservice should manage its own data domain (Database per Service pattern).

---

## Q2: Distributed Transactions
Which pattern is commonly used to handle distributed transactions across multiple microservices without using Two-Phase Commit (2PC)?

A) The Singleton Pattern
B) The Saga Pattern
C) The Proxy Pattern
D) The Factory Pattern

**Answer**: B
**Explanation**: The Saga pattern manages distributed transactions by executing a sequence of local transactions. If one fails, compensating transactions are executed to undo the previous steps.

---

## Q3: API Gateway
What is the primary role of an API Gateway in a microservices ecosystem?

A) To provide a single entry point for clients, routing requests to the appropriate internal microservices.
B) To act as the main database for all microservices.
C) To compile Java code into bytecode.
D) To execute batch processing jobs.

**Answer**: A
**Explanation**: An API Gateway acts as a reverse proxy, handling routing, cross-cutting concerns (authentication, rate limiting), and aggregating responses for clients.